package com.together.buytogether.cache.singleflight;

public enum CacheState {
	NEVER_CACHED("최초 캐시 생성"),
	LOCAL_EXPIRED_REDIS_EXPIRED("로컬 캐시 만료, Redis 캐시 만료"),
	LOCAL_EXPIRED_REDIS_VALID("로컬 캐시 만료, Redis 캐시 유효"),
	LOCAL_VALID("로컬 캐시 유효");

	private final String description;

	CacheState(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
