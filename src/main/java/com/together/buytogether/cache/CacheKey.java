package com.together.buytogether.cache;

public final class CacheKey {

	public static final String PRODUCTS = "product_composite";
	public static final String PRODUCTS_REDIS = "product_redis";
	public static final String TRENDING_REDIS = "trending_redis";
	public static final String TRENDING_COMPOSITE = "trending_composite";
	// CacheName 등록 대상 아님: 싱글플라이트 경로는 LocalCacheManager가 어노테이션 TTL로 캐시를 생성한다
	public static final String TRENDING_SINGLE_FLIGHT = "trending_singleflight";

	private CacheKey() {
	}
}
