package com.together.buytogether.cache;

public enum CacheType {
	LOCAL("로컬 캐시만 사용"),
	GLOBAL("글로벌 캐시만 사용"),
	COMPOSITE("모두 사용");

	private final String description;

	CacheType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
