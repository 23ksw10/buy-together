package com.together.buytogether.product.dto.response;

import java.time.LocalDateTime;

import org.springframework.util.Assert;

import com.together.buytogether.product.domain.ProductStatus;

import lombok.Builder;

@Builder
public record UpdateProductResponseDTO(
	Long productId,
	String title,
	String content,
	String memberName,
	ProductStatus status,
	Long price,
	Long maxQuantity,
	LocalDateTime updatedAt
) {
	public UpdateProductResponseDTO {
		Assert.notNull(productId, "상품 ID는 필수 입니다");
		Assert.hasText(title, "제목은 필수 입니다");
		Assert.hasText(content, "내용은 필수 입니다");
		Assert.hasText(memberName, "판매자 이름은 필수 입니다");
		Assert.notNull(status, "상품 상태는 필수 입니다");
		Assert.notNull(price, "가격은 필수 입니다");
		Assert.notNull(maxQuantity, "최대 판매량은 필수 입니다");
		Assert.notNull(updatedAt, "업데이트 시간은 null일 수 없습니다");
	}
}
