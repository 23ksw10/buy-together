package com.together.buytogether.cache.singleflight;

import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;

import com.together.buytogether.annotation.SingleFlightCacheable;
import com.together.buytogether.cache.dto.SingleFlightCacheData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CacheStrategyExecutor {
	private static final int MAX_CACHE_WAIT_ATTEMPTS = 10;
	private static final long BASE_DELAY_MILLIS = 50L;
	private static final long MAX_DELAY_MILLIS = 500L;
	private final SingleFlightCacheManager singleFlightCacheManager;
	private final AsyncCacheRefresher asyncCacheRefresher;

	public CacheStrategyExecutor(SingleFlightCacheManager singleFlightCacheManager,
		AsyncCacheRefresher asyncCacheRefresher) {
		this.singleFlightCacheManager = singleFlightCacheManager;
		this.asyncCacheRefresher = asyncCacheRefresher;
	}

	public Object executeCacheStrategy(SingleFlightCacheable annotation, String cacheKey,
		ProceedingJoinPoint joinPoint) throws Throwable {
		CacheState state = singleFlightCacheManager.determineCacheState(annotation.cacheName(), cacheKey);

		return switch (state) {
			case NEVER_CACHED, LOCAL_EXPIRED_REDIS_EXPIRED ->
				handleCacheMissWithSingleFlight(cacheKey, annotation, joinPoint);
			case LOCAL_EXPIRED_REDIS_VALID -> handleRedisHitAndRefreshLocal(cacheKey, joinPoint, annotation);
			case LOCAL_VALID -> handleLocalCacheHit(annotation, cacheKey);
		};
	}

	private Object handleCacheMissWithSingleFlight(String cacheKey, SingleFlightCacheable annotation,
		ProceedingJoinPoint joinPoint) throws Throwable {
		if (singleFlightCacheManager.acquireLock(cacheKey)) {
			try {
				return recreateCacheWithLock(annotation, joinPoint, cacheKey);
			} finally {
				singleFlightCacheManager.releaseLock(cacheKey);
			}
		} else {
			return waitForCacheCreation(cacheKey);
		}
	}

	private Object handleLocalCacheHit(SingleFlightCacheable annotation, String cacheKey) {
		String cacheName = annotation.cacheName();
		Object cachedValue = singleFlightCacheManager.getFromLocal(cacheName, cacheKey);
		Object rawValue = (cachedValue instanceof Cache.ValueWrapper)
			? ((Cache.ValueWrapper)cachedValue).get()
			: cachedValue;

		if (rawValue instanceof SingleFlightCacheData) {
			return ((SingleFlightCacheData)rawValue).data();
		}

		return rawValue;
	}

	private Object handleRedisHitAndRefreshLocal(String cacheKey,
		ProceedingJoinPoint joinPoint,
		SingleFlightCacheable annotation) {
		SingleFlightCacheData cacheData = singleFlightCacheManager.getFromRedis(cacheKey);
		singleFlightCacheManager.putToLocal(annotation.cacheName(), cacheKey, cacheData,
			annotation.localTimeToLiveMillis());

		if (shouldRefresh(cacheData)) {
			asyncCacheRefresher.refresh(cacheKey, annotation, joinPoint, cacheData.maxAttemptRefreshCache());
		}
		return cacheData.data();
	}

	private boolean decideToUpdateCache(long createdAt, long timeToLiveMillis, long decisionForUpdate) {
		long currentTime = System.currentTimeMillis();
		long passedDuration = currentTime - createdAt;
		return passedDuration >= timeToLiveMillis * (decisionForUpdate / 100.0);
	}

	private Object waitForCacheCreation(String cacheKey) throws Throwable {
		for (int attempt = 0; attempt < MAX_CACHE_WAIT_ATTEMPTS; attempt++) {
			SingleFlightCacheData cached = singleFlightCacheManager.getFromRedis(cacheKey);
			if (cached != null) {
				return cached.data();
			}
			backoff(attempt);
		}
		throw new RuntimeException("캐시 획득 실패");
	}

	private SingleFlightCacheData createAndExecuteBusinessLogic(SingleFlightCacheable annotation,
		ProceedingJoinPoint joinPoint) throws Throwable {
		return createSingleFlightCacheData(
			joinPoint,
			annotation.decisionForUpdate(),
			annotation.maxAttemptRefreshCache(),
			annotation.redisTimeToLiveMillis()
		);
	}

	private void backoff(int attempt) {
		try {
			long delay = Math.min(
				BASE_DELAY_MILLIS << attempt,
				MAX_DELAY_MILLIS);
			TimeUnit.MILLISECONDS.sleep(delay);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("캐시 대기 중 인터럽트가 발생했습니다", e);
		}
	}

	private SingleFlightCacheData createSingleFlightCacheData(
		ProceedingJoinPoint joinPoint,
		Long decisionForUpdate,
		Long maxAttemptRefreshCache,
		Long timeToLiveMillis) throws Throwable {
		SingleFlightCacheData result = new SingleFlightCacheData<>(joinPoint.proceed(),
			decisionForUpdate,
			maxAttemptRefreshCache,
			System.currentTimeMillis(),
			timeToLiveMillis);
		return result;
	}

	private Object recreateCacheWithLock(SingleFlightCacheable annotation,
		ProceedingJoinPoint joinPoint,
		String cacheKey) throws Throwable {
		SingleFlightCacheData data = createAndExecuteBusinessLogic(annotation, joinPoint);
		singleFlightCacheManager.storeInBothCaches(cacheKey, annotation, data);
		return data.data();
	}

	private boolean shouldRefresh(SingleFlightCacheData cacheData) {
		boolean hasRefreshCountLeft =
			cacheData.maxAttemptRefreshCache() > 0 || cacheData.maxAttemptRefreshCache() == -1;
		if (!hasRefreshCountLeft) {
			return false;
		}

		return decideToUpdateCache(cacheData.createdAt(), cacheData.timeToMillis(), cacheData.decisionForUpdate());
	}
}
