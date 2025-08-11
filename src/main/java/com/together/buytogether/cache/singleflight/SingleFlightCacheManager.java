package com.together.buytogether.cache.singleflight;

import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;

import com.together.buytogether.annotation.SingleFlightCacheable;
import com.together.buytogether.cache.dto.SingleFlightCacheData;
import com.together.buytogether.cache.manager.LocalCacheManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SingleFlightCacheManager {
	private static final Long DEFAULT_LOCK_TIMEOUT_MILLIS = 6000L;
	private final LocalCacheManager localCacheManager;
	private final RedisCacheService redisCacheService;

	public SingleFlightCacheManager(LocalCacheManager localCacheManager, RedisCacheService redisCacheService) {
		this.localCacheManager = localCacheManager;
		this.redisCacheService = redisCacheService;
	}

	public CacheState determineCacheState(String cacheName, String cacheKey) {
		if (isNeverCachedBefore(cacheName)) {
			return CacheState.NEVER_CACHED;
		}

		if (isLocalCacheExpired(cacheName, cacheKey)) {
			if (isRedisCacheExpired(cacheKey)) {
				return CacheState.LOCAL_EXPIRED_REDIS_EXPIRED;
			} else {
				return CacheState.LOCAL_EXPIRED_REDIS_VALID;
			}
		}

		return CacheState.LOCAL_VALID;
	}

	public void putToRedis(String key, SingleFlightCacheData singleFlightCacheData, Long timeToLiveMillis) {
		redisCacheService.put(key, singleFlightCacheData, timeToLiveMillis);
	}

	public SingleFlightCacheData getFromRedis(String cacheKey) {
		return redisCacheService.get(cacheKey);
	}

	public Cache.ValueWrapper getFromLocal(String cacheName, String cacheKey) {
		return localCacheManager.getCache(cacheName).get(cacheKey);
	}

	private boolean isNeverCachedBefore(String cacheName) {
		return localCacheManager.getCache(cacheName) == null;
	}

	private boolean isLocalCacheExpired(String cacheName, String cacheKey) {
		Cache cache = localCacheManager.getCache(cacheName);
		return cache.get(cacheKey) == null;
	}

	private boolean isRedisCacheExpired(String cacheKey) {
		return redisCacheService.get(cacheKey) == null;
	}

	public boolean acquireLock(String cacheKey) {
		String lockKey = cacheKey + "::lock";
		return redisCacheService.getLock(lockKey, DEFAULT_LOCK_TIMEOUT_MILLIS);
	}

	public void releaseLock(String cacheKey) {
		redisCacheService.unlock(cacheKey);
	}

	public void storeInBothCaches(String cacheKey,
		SingleFlightCacheable annotation,
		SingleFlightCacheData data) {
		log.info(cacheKey);
		redisCacheService.put(cacheKey, data, annotation.redisTimeToLiveMillis());
		localCacheManager.putToCache(annotation.cacheName(), cacheKey, data, annotation.localTimeToLiveMillis());
	}

	public void evictFromLocal(String cacheName, String cacheKey) {
		localCacheManager.getCache(cacheName).evict(cacheKey);
	}

	public void evictFromRedis(String cacheKey) {
		redisCacheService.evict(cacheKey);
	}
}
