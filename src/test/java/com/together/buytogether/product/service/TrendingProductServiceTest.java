package com.together.buytogether.product.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.together.buytogether.product.domain.TrendingProductRepository;
import com.together.buytogether.product.domain.TrendingProductRow;
import com.together.buytogether.product.dto.response.TrendingProductResponseDTO;

@DisplayName("TrendingProduct Service 테스트")
@ExtendWith(MockitoExtension.class)
class TrendingProductServiceTest {

	@InjectMocks
	private TrendingProductService trendingProductService;

	@Mock
	private TrendingProductRepository trendingProductRepository;

	@Test
	@DisplayName("v1은 기본 product_id 인덱스 조회 결과를 응답으로 변환한다")
	void getTrendingV1() {
		List<TrendingProductRow> rows = trendingRows();
		given(trendingProductRepository.findTrendingTop100UsingBasicIndexes(any(LocalDateTime.class))).willReturn(rows);

		List<TrendingProductResponseDTO> result = trendingProductService.getTrendingV1();

		assertMappedInRepositoryOrder(result);
		ArgumentCaptor<LocalDateTime> cutoffCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
		then(trendingProductRepository).should().findTrendingTop100UsingBasicIndexes(cutoffCaptor.capture());
		assertSevenDayWindow(cutoffCaptor.getValue());
		then(trendingProductRepository).shouldHaveNoMoreInteractions();
	}

	@Test
	@DisplayName("v2는 최적화된 조회 결과를 응답으로 변환한다")
	void getTrendingV2() {
		assertOptimizedQuery(trendingProductService::getTrendingV2);
	}

	@Test
	@DisplayName("v3는 최적화된 조회 결과를 응답으로 변환한다")
	void getTrendingV3() {
		assertOptimizedQuery(trendingProductService::getTrendingV3);
	}

	@Test
	@DisplayName("v4는 최적화된 조회 결과를 응답으로 변환한다")
	void getTrendingV4() {
		assertOptimizedQuery(trendingProductService::getTrendingV4);
	}

	@Test
	@DisplayName("v5는 최적화된 조회 결과를 응답으로 변환한다")
	void getTrendingV5() {
		assertOptimizedQuery(trendingProductService::getTrendingV5);
	}

	private void assertOptimizedQuery(Supplier<List<TrendingProductResponseDTO>> query) {
		List<TrendingProductRow> rows = trendingRows();
		given(trendingProductRepository.findTrendingTop100(any(LocalDateTime.class))).willReturn(rows);

		List<TrendingProductResponseDTO> result = query.get();

		assertMappedInRepositoryOrder(result);
		ArgumentCaptor<LocalDateTime> cutoffCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
		then(trendingProductRepository).should().findTrendingTop100(cutoffCaptor.capture());
		assertSevenDayWindow(cutoffCaptor.getValue());
		then(trendingProductRepository).shouldHaveNoMoreInteractions();
	}

	private void assertSevenDayWindow(LocalDateTime cutoff) {
		LocalDateTime expected = LocalDateTime.now().minusDays(7);
		assertThat(cutoff).isBetween(expected.minusMinutes(1), expected.plusMinutes(1));
	}

	private void assertMappedInRepositoryOrder(List<TrendingProductResponseDTO> result) {
		assertThat(result).containsExactly(
			new TrendingProductResponseDTO(1L, "첫 번째 상품", 10_000L, 3L, 2L, 8L),
			new TrendingProductResponseDTO(2L, "두 번째 상품", 20_000L, 1L, 4L, 6L)
		);
	}

	private List<TrendingProductRow> trendingRows() {
		return List.of(
			trendingRow(1L, "첫 번째 상품", 10_000L, 3L, 2L, 8L),
			trendingRow(2L, "두 번째 상품", 20_000L, 1L, 4L, 6L)
		);
	}

	private TrendingProductRow trendingRow(
		Long productId,
		String title,
		Long price,
		Long enrollCount,
		Long likeCount,
		Long score
	) {
		TrendingProductRow row = mock(TrendingProductRow.class);
		given(row.getProductId()).willReturn(productId);
		given(row.getTitle()).willReturn(title);
		given(row.getPrice()).willReturn(price);
		given(row.getEnrollCount()).willReturn(enrollCount);
		given(row.getLikeCount()).willReturn(likeCount);
		given(row.getScore()).willReturn(score);
		return row;
	}
}
