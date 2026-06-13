package com.together.buytogether.cache.singleflight;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.together.buytogether.annotation.SingleFlightCacheable;
import com.together.buytogether.cache.dto.SingleFlightCacheData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AsyncCacheRefresher {
	private final SingleFlightCacheManager singleFlightCacheManager;

	public AsyncCacheRefresher(SingleFlightCacheManager singleFlightCacheManager) {
		this.singleFlightCacheManager = singleFlightCacheManager;
	}

	@Async // 이 메소드는 별도의 스레드에서 비동기적으로 실행됩니다.
	public void refresh(String cacheKey, SingleFlightCacheable annotation, ProceedingJoinPoint joinPoint,
		long remainingRefreshAttempts) {
		if (singleFlightCacheManager.acquireLock(cacheKey)) {
			try {
				log.info("비동기 캐시 갱신 시작. cacheName={}, cacheKey={}, remainingRefreshAttempts={}",
					annotation.cacheName(), cacheKey, remainingRefreshAttempts);
				SingleFlightCacheData<?> newData = recreateSingleFlightCache(joinPoint, annotation,
					remainingRefreshAttempts);
				singleFlightCacheManager.storeInBothCaches(cacheKey, annotation, newData);
				log.info("비동기 캐시 갱신 끝. cacheName={}, cacheKey={}, remainingRefreshAttempts={}",
					annotation.cacheName(), cacheKey, remainingRefreshAttempts);
			} catch (Throwable e) {
				log.error("비동기 캐시 갱신 실패. cacheName={}, cacheKey={}, remainingRefreshAttempts={}",
					annotation.cacheName(), cacheKey, remainingRefreshAttempts, e);
			} finally {
				singleFlightCacheManager.releaseLock(cacheKey);
			}
		}
	}

	private SingleFlightCacheData<?> recreateSingleFlightCache(ProceedingJoinPoint joinPoint,
		SingleFlightCacheable annotation,
		long remainingRefreshAttempts) throws Throwable {
		Object refreshedData = joinPoint.proceed();

		return new SingleFlightCacheData<>(
			refreshedData,
			annotation.decisionForUpdate(),
			nextRefreshAttemptCount(remainingRefreshAttempts),
			System.currentTimeMillis(),
			annotation.redisTimeToLiveMillis()
		);
	}

	private long nextRefreshAttemptCount(long remainingRefreshAttempts) {
		if (remainingRefreshAttempts == -1) {
			return -1;
		}

		return Math.max(0, remainingRefreshAttempts - 1);
	}

}
