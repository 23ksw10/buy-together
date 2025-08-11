package com.together.buytogether.cache.singleflight;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
	private static final long BASE_DELAY_MILLIS = 1000L;
	private static final long MAX_DELAY_MILLIS = 100_000L;
	private final SingleFlightCacheManager singleFlightCacheManager;

	public CacheStrategyExecutor(SingleFlightCacheManager singleFlightCacheManager) {
		this.singleFlightCacheManager = singleFlightCacheManager;
	}

	public Object executeCacheStrategy(SingleFlightCacheable annotation, String cacheKey,
		ProceedingJoinPoint joinPoint) throws Throwable {
		CacheState state = singleFlightCacheManager.determineCacheState(annotation.cacheName(), cacheKey);

		return switch (state) {
			case NEVER_CACHED -> handleNeverCached(annotation, joinPoint, cacheKey);
			case LOCAL_EXPIRED_REDIS_EXPIRED -> handleBothExpired(cacheKey, annotation, joinPoint);
			case LOCAL_EXPIRED_REDIS_VALID -> handleLocalExpiredOnly(cacheKey, joinPoint, annotation);
			case LOCAL_VALID -> handleLocalValid(annotation, cacheKey);
		};
	}

	private Object handleBothExpired(String cacheKey, SingleFlightCacheable annotation,
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

	private Object handleLocalValid(SingleFlightCacheable annotation, String cacheKey) {
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

	private Object handleLocalExpiredOnly(String cacheKey,
		ProceedingJoinPoint joinPoint,
		SingleFlightCacheable annotation) throws Throwable {
		SingleFlightCacheData cacheData = singleFlightCacheManager.getFromRedis(cacheKey);

		if (shouldRefresh(cacheData)) {
			if (singleFlightCacheManager.acquireLock(cacheKey)) {
				SingleFlightCacheData data = createAndExecuteBusinessLogic(annotation, joinPoint);
				singleFlightCacheManager.storeInBothCaches(cacheKey, annotation, data);
			}
			return cacheData.data();
		}

		return cacheData.data();
	}

	private Object handleNeverCached(SingleFlightCacheable annotation, ProceedingJoinPoint joinPoint,
		String cacheKey) throws Throwable {
		SingleFlightCacheData result = createAndExecuteBusinessLogic(annotation, joinPoint);
		singleFlightCacheManager.storeInBothCaches(cacheKey, annotation, result);
		return result.data();
	}

	private boolean decideToUpdateCache(long createdAt, long timeToLiveMillis, long decisionForUpdate) {
		long currentTime = System.currentTimeMillis();
		long passedDuration = currentTime - createdAt;
		return passedDuration >= timeToLiveMillis * (decisionForUpdate / 100.0);
	}

	private Object waitForCacheCreation(String cacheKey) throws Throwable {
		for (int attempt = 0; attempt < 10; attempt++) {
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
			LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
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
