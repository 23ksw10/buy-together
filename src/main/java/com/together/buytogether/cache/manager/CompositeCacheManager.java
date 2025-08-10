package com.together.buytogether.cache.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.together.buytogether.cache.CacheName;
import com.together.buytogether.cache.CompositeCache;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompositeCacheManager implements CacheManager {

	private final List<CacheManager> cacheManagers;
	private final UpdatableCacheManager updatableCacheManager;
	private final List<String> cacheNames;

	public CompositeCacheManager(
		List<CacheManager> cacheManagers,
		UpdatableCacheManager updatableCacheManager) {
		this.cacheManagers = new ArrayList<>(cacheManagers);
		this.updatableCacheManager = updatableCacheManager;

		List<String> names = new ArrayList<>();
		for (CacheManager cacheManager : cacheManagers) {
			names.addAll(cacheManager.getCacheNames());
		}
		this.cacheNames = names;
	}

	@Override
	public Cache getCache(String name) {
		if (CacheName.isCompositeType(name)) {
			log.info("composite 캐시 조회");
			List<Cache> caches = cacheManagers.stream()
				.map(cacheManager -> cacheManager.getCache(name))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
			return new CompositeCache(caches, updatableCacheManager);
		}

		return cacheManagers.stream()
			.map(cacheManager -> cacheManager.getCache(name))
			.filter(Objects::nonNull)
			.findFirst()
			.orElse(null);
	}

	@Override
	public List<String> getCacheNames() {
		return new ArrayList<>(cacheNames);
	}

}
