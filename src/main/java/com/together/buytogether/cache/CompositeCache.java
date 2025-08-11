package com.together.buytogether.cache;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.springframework.cache.Cache;

import com.together.buytogether.cache.manager.UpdatableCacheManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompositeCache implements Cache {

	private final List<Cache> caches;
	private final UpdatableCacheManager updatableCacheManager;

	public CompositeCache(List<Cache> caches, UpdatableCacheManager updatableCacheManager) {
		this.caches = caches;
		this.updatableCacheManager = updatableCacheManager;
	}

	@Override
	public String getName() {
		return caches.get(0).getName();
	}

	@Override
	public Object getNativeCache() {
		return caches.stream()
			.map(Cache::getNativeCache)
			.collect(Collectors.toList());
	}

	@Override
	public ValueWrapper get(Object key) {
		for (Cache cache : caches) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() != null) {
				updatableCacheManager.putIfAbsent(cache, key, valueWrapper.get()); // L1 채우기
				return valueWrapper;
			}
		}
		return null;
	}

	@Override
	public <T> T get(Object key, Class<T> type) {
		for (Cache cache : caches) {

		}
		return null;
	}

	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		for (Cache cache : caches) {
			T value = cache.get(key, valueLoader);
			if (value != null) {
				updatableCacheManager.putIfAbsent(cache, key, value);
				return value;
			}
		}
		return null;
	}

	@Override
	public void put(Object key, Object value) {
		for (Cache cache : caches) {
			cache.put(key, value);
		}
	}

	@Override
	public void evict(Object key) {
		for (Cache cache : caches) {
			log.info("composite evict : {} ", cache.getName());
			cache.evict(key);
		}
	}

	@Override
	public void clear() {
		for (Cache cache : caches) {
			cache.clear();
		}
	}
}
