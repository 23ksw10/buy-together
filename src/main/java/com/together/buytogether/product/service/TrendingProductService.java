package com.together.buytogether.product.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	private List<TrendingProductResponseDTO> toResponse(List<TrendingProductRow> rows) {
		return rows.stream()
			.map(TrendingProductResponseDTO::from)
			.toList();
	}
}
