package com.together.buytogether.cache.manager;

import org.springframework.cache.Cache;

public interface UpdatableCacheManager {
	void putIfAbsent(Cache cache, Object key, Object value);
}
