package com.together.buytogether.product.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
	// 트렌딩 = 최근 7일간의 참여/좋아요 활동 집계 (전체 누적이 아닌 최근 인기 급상승)
	private static final int TRENDING_WINDOW_DAYS = 7;

	private final TrendingProductRepository trendingProductRepository;

	public TrendingProductService(TrendingProductRepository trendingProductRepository) {
		this.trendingProductRepository = trendingProductRepository;
	}

	// v1: 베이스라인 — 단일 컬럼 product_id 인덱스 사용 후 시간/상태 조건은 추가 필터링
	@Transactional(readOnly = true)
	public List<TrendingProductResponseDTO> getTrendingV1() {
		return toResponse(trendingProductRepository.findTrendingTop100UsingBasicIndexes(trendingCutoff()));
	}

	// v2: v1과 동일한 쿼리 + 커버링 인덱스 (enroll(product_id, created_at), product_like(product_id, like_status, updated_at))
	@Transactional(readOnly = true)
	public List<TrendingProductResponseDTO> getTrendingV2() {
		return toResponse(trendingProductRepository.findTrendingTop100(trendingCutoff()));
	}

	// v3: Redis 단일 캐시 (TTL 30초) — 만료 순간 동시 요청이 모두 DB로 몰리는 한계 존재
	@Cacheable(cacheNames = CacheKey.TRENDING_REDIS, key = "'top100'", cacheManager = "redisCacheManager")
	@Transactional(readOnly = true)
	public List<TrendingProductResponseDTO> getTrendingV3() {
		return toResponse(trendingProductRepository.findTrendingTop100(trendingCutoff()));
	}

	// v4: Caffeine(L1) + Redis(L2) 2계층 캐시 — L1 히트 시 네트워크 왕복 제거, L2 히트 시 L1 재적재
	// CompositeCache의 get(key, valueLoader)가 L1만 채우므로 sync=true는 사용하지 않는다
	@Cacheable(cacheNames = CacheKey.TRENDING_COMPOSITE, key = "'top100'")
	@Transactional(readOnly = true)
	public List<TrendingProductResponseDTO> getTrendingV4() {
		return toResponse(trendingProductRepository.findTrendingTop100(trendingCutoff()));
	}

	// v5: 싱글플라이트 — 캐시 만료 시 한 요청만 DB를 조회하고 나머지는 대기(스탬피드 방지),
	// Redis TTL의 80% 경과 후 히트 시 비동기 갱신(stale-while-revalidate)
	@SingleFlightCacheable(
		cacheName = CacheKey.TRENDING_SINGLE_FLIGHT,
		key = "'top100'",
		localTimeToLiveMillis = 5_000,
		redisTimeToLiveMillis = 30_000,
		decisionForUpdate = 80
	)
	@Transactional(readOnly = true)
	public List<TrendingProductResponseDTO> getTrendingV5() {
		return toResponse(trendingProductRepository.findTrendingTop100(trendingCutoff()));
	}

	// 캐시 키는 'top100' 고정이므로 cutoff는 캐시 미스 시점에만 평가된다
	private LocalDateTime trendingCutoff() {
		return LocalDateTime.now().minusDays(TRENDING_WINDOW_DAYS);
	}

	// Stream.toList()가 반환하는 java.util 불변 리스트(final 타입)는 GenericJackson2JsonRedisSerializer가
	// 타입 정보 없이 직렬화해 역직렬화에 실패하므로, 타입 래퍼가 기록되는 ArrayList로 수집한다
	private List<TrendingProductResponseDTO> toResponse(List<TrendingProductRow> rows) {
		return rows.stream()
			.map(TrendingProductResponseDTO::from)
			.collect(Collectors.toList());
	}
}
