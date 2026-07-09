package com.together.buytogether.product.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.together.buytogether.annotation.SingleFlightCacheable;
import com.together.buytogether.cache.CacheKey;
import com.together.buytogether.product.domain.TrendingProductRepository;
import com.together.buytogether.product.domain.TrendingProductRow;
import com.together.buytogether.product.dto.response.TrendingProductResponseDTO;

@Service
public class TrendingProductService {
	private final TrendingProductRepository trendingProductRepository;

	public TrendingProductService(TrendingProductRepository trendingProductRepository) {
		this.trendingProductRepository = trendingProductRepository;
	}

	// v1: 베이스라인 — 조인/집계 쿼리를 매 요청마다 실행 (인덱스 미사용 실행 계획 고정)
	@Transactional(readOnly = true)
	public List<TrendingProductResponseDTO> getTrendingV1() {
		return toResponse(trendingProductRepository.findTrendingTop10IgnoringIndexes());
	}

	// v2: v1과 동일한 쿼리 + 인덱스 (enroll.product_id, product_like(product_id, like_status))
	@Transactional(readOnly = true)
	public List<TrendingProductResponseDTO> getTrendingV2() {
		return toResponse(trendingProductRepository.findTrendingTop10());
	}

	// v3: Redis 단일 캐시 (TTL 30초) — 만료 순간 동시 요청이 모두 DB로 몰리는 한계 존재
	@Cacheable(cacheNames = CacheKey.TRENDING_REDIS, key = "'top10'", cacheManager = "redisCacheManager")
	@Transactional(readOnly = true)
	public List<TrendingProductResponseDTO> getTrendingV3() {
		return toResponse(trendingProductRepository.findTrendingTop10());
	}

	// v4: Caffeine(L1) + Redis(L2) 2계층 캐시 — L1 히트 시 네트워크 왕복 제거, L2 히트 시 L1 재적재
	// CompositeCache의 get(key, valueLoader)가 L1만 채우므로 sync=true는 사용하지 않는다
	@Cacheable(cacheNames = CacheKey.TRENDING_COMPOSITE, key = "'top10'")
	@Transactional(readOnly = true)
	public List<TrendingProductResponseDTO> getTrendingV4() {
		return toResponse(trendingProductRepository.findTrendingTop10());
	}

	// v5: 싱글플라이트 — 캐시 만료 시 한 요청만 DB를 조회하고 나머지는 대기(스탬피드 방지),
	// Redis TTL의 80% 경과 후 히트 시 비동기 갱신(stale-while-revalidate)
	@SingleFlightCacheable(
		cacheName = CacheKey.TRENDING_SINGLE_FLIGHT,
		key = "'top10'",
		localTimeToLiveMillis = 5_000,
		redisTimeToLiveMillis = 30_000,
		decisionForUpdate = 80
	)
	@Transactional(readOnly = true)
	public List<TrendingProductResponseDTO> getTrendingV5() {
		return toResponse(trendingProductRepository.findTrendingTop10());
	}

	private List<TrendingProductResponseDTO> toResponse(List<TrendingProductRow> rows) {
		return rows.stream()
			.map(TrendingProductResponseDTO::from)
			.toList();
	}
}
