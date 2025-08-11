package com.together.buytogether.cache.manager;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;

import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalCacheManager implements CacheManager, UpdatableCacheManager {

	private final Map<String, Cache> cacheMap = new ConcurrentHashMap<>();

	public LocalCacheManager() {

	}

	public LocalCacheManager(List<Cache> caches) {
		for (Cache cache : caches) {
			cacheMap.put(cache.getName(), cache);
		}
	}

	@Override
	public Cache getCache(String name) {
		log.info("로컬 캐시 조회 : {}", name);
		return cacheMap.get(name);
	}

	public Collection<String> getCacheNames() {
		return cacheMap.keySet();
	}

	@Override
	public void putIfAbsent(Cache cache, Object key, Object value) {
		Cache local = getCache(cache.getName());
		if (local != null) {
			local.putIfAbsent(key, value);
		}
	}

	public Cache getOrCreateCache(String cacheName, Long timeToLiveMillis) {
		return cacheMap.computeIfAbsent(cacheName, name -> {
			log.info("새로운 로컬 캐시 생성: {} (TTL: {}ms)", name, timeToLiveMillis);

			return new CaffeineCache(
				name,
				Caffeine.newBuilder()
					.expireAfterWrite(Duration.ofMillis(timeToLiveMillis))
					.recordStats()
					.build()
			);
		});
	}

	public void putToCache(String cacheName, String key, Object value, Long timeToLiveMillis) {
		Cache cache = getOrCreateCache(cacheName, timeToLiveMillis);
		cache.put(key, value);
		log.info("로컬 캐시 저장: {} -> {}", cacheName, key);
	}

}
