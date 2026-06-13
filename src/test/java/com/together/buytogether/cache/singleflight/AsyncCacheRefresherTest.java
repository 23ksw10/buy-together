package com.together.buytogether.cache.singleflight;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.together.buytogether.annotation.SingleFlightCacheable;
import com.together.buytogether.cache.dto.SingleFlightCacheData;

@ExtendWith(MockitoExtension.class)
class AsyncCacheRefresherTest {

	@Mock
	private SingleFlightCacheManager singleFlightCacheManager;

	@Mock
	private SingleFlightCacheable annotation;

	@Mock
	private ProceedingJoinPoint joinPoint;

	@Captor
	private ArgumentCaptor<SingleFlightCacheData<?>> cacheDataCaptor;

	private AsyncCacheRefresher asyncCacheRefresher;

	@BeforeEach
	void setUp() {
		asyncCacheRefresher = new AsyncCacheRefresher(singleFlightCacheManager);
	}

	@ParameterizedTest
	@CsvSource({
		"3, 2",
		"1, 0",
		"0, 0",
		"-1, -1"
	})
	void refreshStoresNextRemainingRefreshAttemptCount(long remainingRefreshAttempts,
		long expectedRemainingRefreshAttempts) throws Throwable {
		when(singleFlightCacheManager.acquireLock("cache-key")).thenReturn(true);
		when(annotation.cacheName()).thenReturn("cache-name");
		when(annotation.decisionForUpdate()).thenReturn(90L);
		when(annotation.redisTimeToLiveMillis()).thenReturn(1000L);
		when(joinPoint.proceed()).thenReturn("refreshed-value");

		asyncCacheRefresher.refresh("cache-key", annotation, joinPoint, remainingRefreshAttempts);

		verify(singleFlightCacheManager).storeInBothCaches(eq("cache-key"), eq(annotation), cacheDataCaptor.capture());
		verify(singleFlightCacheManager).releaseLock("cache-key");
		assertThat(cacheDataCaptor.getValue().data()).isEqualTo("refreshed-value");
		assertThat(cacheDataCaptor.getValue().maxAttemptRefreshCache()).isEqualTo(expectedRemainingRefreshAttempts);
	}

	@Test
	void refreshDoesNothingWhenLockIsNotAcquired() throws Throwable {
		when(singleFlightCacheManager.acquireLock("cache-key")).thenReturn(false);

		asyncCacheRefresher.refresh("cache-key", annotation, joinPoint, 3L);

		verify(singleFlightCacheManager).acquireLock("cache-key");
		verify(singleFlightCacheManager, never())
			.storeInBothCaches(eq("cache-key"), eq(annotation), any());
		verify(singleFlightCacheManager, never()).releaseLock("cache-key");
		verifyNoInteractions(joinPoint);
	}

	@Test
	void refreshLogsFailureAndReleasesLockWhenBusinessLogicThrows() throws Throwable {
		RuntimeException exception = new RuntimeException("boom");

		when(singleFlightCacheManager.acquireLock("cache-key")).thenReturn(true);
		when(annotation.cacheName()).thenReturn("cache-name");
		when(joinPoint.proceed()).thenThrow(exception);

		asyncCacheRefresher.refresh("cache-key", annotation, joinPoint, 3L);

		verify(singleFlightCacheManager, never())
			.storeInBothCaches(eq("cache-key"), eq(annotation), any());
		verify(singleFlightCacheManager).releaseLock("cache-key");
	}
}
