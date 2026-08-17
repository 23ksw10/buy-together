package com.together.buytogether.product.dto.response;

import org.springframework.util.Assert;

import com.together.buytogether.product.domain.TrendingProductRow;

import lombok.Builder;

public record TrendingProductResponseDTO(
	Long productId,
	String title,
	Long price,
	Long enrollCount,
	Long likeCount,
	Long score
) {
	@Builder
	public TrendingProductResponseDTO {
		Assert.notNull(productId, "상품 번호는 필수 값입니다");
		Assert.hasText(title, "상품 제목은 필수 값입니다");
		Assert.notNull(price, "상품 가격은 필수 값입니다");
		Assert.notNull(enrollCount, "참여 수는 필수 값입니다");
		Assert.notNull(likeCount, "좋아요 수는 필수 값입니다");
		Assert.notNull(score, "인기 점수는 필수 값입니다");
	}

	public static TrendingProductResponseDTO from(TrendingProductRow row) {
		return TrendingProductResponseDTO.builder()
			.productId(row.getProductId())
			.title(row.getTitle())
			.price(row.getPrice())
			.enrollCount(row.getEnrollCount())
			.likeCount(row.getLikeCount())
			.score(row.getScore())
			.build();
	}
}
