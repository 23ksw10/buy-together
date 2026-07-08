package com.together.buytogether.product.dto.response;

import java.time.LocalDateTime;

import org.springframework.util.Assert;

import com.together.buytogether.product.domain.Product;
import com.together.buytogether.product.domain.ProductStatus;

import lombok.Builder;

public record ProductResponseDTO(
	String memberName,
	Long productId,
	String title,
	String content,
	ProductStatus status,
	LocalDateTime expiredAt,
	Long price,
	Long soldQuantity,
	Long maxQuantity
) {
	@Builder
	public ProductResponseDTO {
		Assert.hasText(memberName, "회원 이름은 필수 값입니다");
		Assert.notNull(productId, "상품 번호는 필수 값입니다");
		Assert.hasText(title, "상품 제목은 필수 값입니다");
		Assert.hasText(content, "상품 설명은 필수 값입니다");
		Assert.notNull(status, "상품 상태는 필수 값입니다");
		Assert.notNull(expiredAt, "만료일은 필수 값입니다");
		Assert.notNull(price, "상품 가격은 필수 값입니다");
		Assert.notNull(soldQuantity, "판매량은 필수 값입니다");
		Assert.notNull(maxQuantity, "최대 판매량은 필수 값입니다");
	}

	public static ProductResponseDTO from(Product product) {
		return ProductResponseDTO.builder()
			.memberName(product.getMember().getName())
			.productId(product.getProductId())
			.title(product.getTitle())
			.content(product.getContent())
			.status(product.getStatus())
			.expiredAt(product.getExpiredAt())
			.price(product.getPrice())
			.soldQuantity(product.getSoldQuantity())
			.maxQuantity(product.getMaxQuantity())
			.build();
	}
}
