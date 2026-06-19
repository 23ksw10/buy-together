package com.together.buytogether.cache.singleflight;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.together.buytogether.annotation.SingleFlightCacheable;
import com.together.buytogether.cache.dto.SingleFlightCacheData;

@ExtendWith(MockitoExtension.class)
class CacheStrategyExecutorTest {

	@Mock
	private SingleFlightCacheManager singleFlightCacheManager;

	@Mock
	private AsyncCacheRefresher asyncCacheRefresher;

	@Mock
	private SingleFlightCacheable annotation;

	@Mock
	private ProceedingJoinPoint joinPoint;

	private CacheStrategyExecutor cacheStrategyExecutor;

	@BeforeEach
	void setUp() {
		cacheStrategyExecutor = new CacheStrategyExecutor(singleFlightCacheManager, asyncCacheRefresher);
	}

	@Test
	void redisHitRestoresLocalCacheWithoutAsyncRefreshWhenCacheIsNotOldEnough() throws Throwable {
		SingleFlightCacheData<String> cacheData = new SingleFlightCacheData<>(
			"redis-value",
			90L,
			3L,
			System.currentTimeMillis(),
			10_000L
		);
		when(annotation.cacheName()).thenReturn("cache-name");
		when(annotation.localTimeToLiveMillis()).thenReturn(1_000L);
		when(singleFlightCacheManager.determineCacheState("cache-name", "cache-key"))
			.thenReturn(CacheState.LOCAL_EXPIRED_REDIS_VALID);
		when(singleFlightCacheManager.getFromRedis("cache-key")).thenReturn(cacheData);

		Object result = cacheStrategyExecutor.executeCacheStrategy(annotation, "cache-key", joinPoint);

		assertThat(result).isEqualTo("redis-value");
		verify(singleFlightCacheManager).putToLocal("cache-name", "cache-key", cacheData, 1_000L);
		verifyNoInteractions(asyncCacheRefresher);
	}

	@Test
	void redisHitRestoresLocalCacheBeforeAsyncRefreshWhenCacheIsOldEnough() throws Throwable {
		SingleFlightCacheData<String> cacheData = new SingleFlightCacheData<>(
			"redis-value",
			70L,
			3L,
			System.currentTimeMillis() - 8_000L,
			10_000L
		);
		when(annotation.cacheName()).thenReturn("cache-name");
		when(annotation.localTimeToLiveMillis()).thenReturn(1_000L);
		when(singleFlightCacheManager.determineCacheState("cache-name", "cache-key"))
			.thenReturn(CacheState.LOCAL_EXPIRED_REDIS_VALID);
		when(singleFlightCacheManager.getFromRedis("cache-key")).thenReturn(cacheData);

		Object result = cacheStrategyExecutor.executeCacheStrategy(annotation, "cache-key", joinPoint);

		assertThat(result).isEqualTo("redis-value");
		InOrder inOrder = inOrder(singleFlightCacheManager, asyncCacheRefresher);
		inOrder.verify(singleFlightCacheManager).putToLocal("cache-name", "cache-key", cacheData, 1_000L);
		inOrder.verify(asyncCacheRefresher).refresh("cache-key", annotation, joinPoint, 3L);
	}

	@Test
	void redisHitRestoresLocalCacheWithoutAsyncRefreshWhenRefreshCountIsZero() throws Throwable {
		SingleFlightCacheData<String> cacheData = new SingleFlightCacheData<>(
			"redis-value",
			70L,
			0L,
			System.currentTimeMillis() - 8_000L,
			10_000L
		);
		when(annotation.cacheName()).thenReturn("cache-name");
		when(annotation.localTimeToLiveMillis()).thenReturn(1_000L);
		when(singleFlightCacheManager.determineCacheState("cache-name", "cache-key"))
			.thenReturn(CacheState.LOCAL_EXPIRED_REDIS_VALID);
		when(singleFlightCacheManager.getFromRedis("cache-key")).thenReturn(cacheData);

		Object result = cacheStrategyExecutor.executeCacheStrategy(annotation, "cache-key", joinPoint);

		assertThat(result).isEqualTo("redis-value");
		verify(singleFlightCacheManager).putToLocal("cache-name", "cache-key", cacheData, 1_000L);
		verify(asyncCacheRefresher, never()).refresh("cache-key", annotation, joinPoint, 0L);
	}

	@Test
	void cacheMissWaitsForRedisWhenLockIsNotAcquired() throws Throwable {
		SingleFlightCacheData<String> cacheData = new SingleFlightCacheData<>(
			"created-value",
			90L,
			3L,
			System.currentTimeMillis(),
			10_000L
		);
		when(annotation.cacheName()).thenReturn("cache-name");
		when(singleFlightCacheManager.determineCacheState("cache-name", "cache-key"))
			.thenReturn(CacheState.LOCAL_EXPIRED_REDIS_EXPIRED);
		when(singleFlightCacheManager.acquireLock("cache-key")).thenReturn(false);
		when(singleFlightCacheManager.getFromRedis("cache-key"))
			.thenReturn(null)
			.thenReturn(cacheData);

		Object result = cacheStrategyExecutor.executeCacheStrategy(annotation, "cache-key", joinPoint);

		assertThat(result).isEqualTo("created-value");
		verify(singleFlightCacheManager, times(2)).getFromRedis("cache-key");
		verifyNoInteractions(asyncCacheRefresher);
	}

	@Test
	void cacheMissFailsAfterBoundedWaitWhenLockIsNotAcquiredAndRedisIsEmpty() {
		when(annotation.cacheName()).thenReturn("cache-name");
		when(singleFlightCacheManager.determineCacheState("cache-name", "cache-key"))
			.thenReturn(CacheState.LOCAL_EXPIRED_REDIS_EXPIRED);
		when(singleFlightCacheManager.acquireLock("cache-key")).thenReturn(false);
		when(singleFlightCacheManager.getFromRedis("cache-key")).thenReturn(null);

		assertThatThrownBy(() -> cacheStrategyExecutor.executeCacheStrategy(annotation, "cache-key", joinPoint))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("캐시 획득 실패");

		verify(singleFlightCacheManager, times(10)).getFromRedis("cache-key");
		verifyNoInteractions(asyncCacheRefresher);
	}

	@Test
	void cacheMissStopsWaitingWhenThreadIsInterrupted() {
		when(annotation.cacheName()).thenReturn("cache-name");
		when(singleFlightCacheManager.determineCacheState("cache-name", "cache-key"))
			.thenReturn(CacheState.LOCAL_EXPIRED_REDIS_EXPIRED);
		when(singleFlightCacheManager.acquireLock("cache-key")).thenReturn(false);
		when(singleFlightCacheManager.getFromRedis("cache-key")).thenReturn(null);

		Thread.currentThread().interrupt();
		try {
			assertThatThrownBy(() -> cacheStrategyExecutor.executeCacheStrategy(annotation, "cache-key", joinPoint))
				.isInstanceOf(RuntimeException.class)
				.hasMessage("캐시 대기 중 인터럽트가 발생했습니다");

			verify(singleFlightCacheManager).getFromRedis("cache-key");
			verifyNoInteractions(asyncCacheRefresher);
			assertThat(Thread.currentThread().isInterrupted()).isTrue();
		} finally {
			Thread.interrupted();
		}
	}
}
