package com.together.buytogether.cache;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CacheName {

	POST_COMPOSITE_CACHE(
		CacheKey.POSTS,
		Duration.ofDays(1),
		CacheType.COMPOSITE
	),

	POST_REDIS_CACHE(
		CacheKey.POSTS_REDIS,
		Duration.ofSeconds(20),
		CacheType.GLOBAL
	);

	private static final Map<String, CacheName> CACHE_NAME_MAP;

	static {
		CACHE_NAME_MAP = Arrays.stream(CacheName.values())
			.collect(Collectors.toMap(CacheName::getCacheName, Function.identity()));
	}

	private final String cacheName;
	private final Duration expiredAfterWrite;
	private final CacheType cacheType;

	CacheName(String cacheName, Duration expiredAfterWrite, CacheType cacheType) {
		this.cacheName = cacheName;
		this.expiredAfterWrite = expiredAfterWrite;
		this.cacheType = cacheType;
	}

	public static boolean isCompositeType(String cacheName) {
		return get(cacheName).getCacheType() == CacheType.COMPOSITE;
	}

	private static CacheName get(String cacheName) {
		CacheName foundCacheName = CACHE_NAME_MAP.get(cacheName);
		if (foundCacheName == null) {
			throw new NoSuchElementException(cacheName + " Cache Name Not Found");
		}
		return foundCacheName;
	}

	public String getCacheName() {
		return cacheName;
	}

	public Duration getExpiredAfterWrite() {
		return expiredAfterWrite;
	}

	public CacheType getCacheType() {
		return cacheType;
	}
}

