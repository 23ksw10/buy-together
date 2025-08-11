package com.together.buytogether.cache.dto;

public record CacheEvictMessage(
	String cacheName,
	Object key) {
}
