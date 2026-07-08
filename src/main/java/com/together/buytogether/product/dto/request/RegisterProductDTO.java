package com.together.buytogether.product.dto.request;

import java.time.LocalDateTime;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.product.domain.Product;
import com.together.buytogether.product.domain.ProductStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RegisterProductDTO(
	@NotBlank(message = "상품 제목은 필수입니다")
	String title,
	@NotBlank(message = "상품 설명은 필수입니다")
	String content,
	@NotNull(message = "상품 상태는 필수입니다")
	ProductStatus status,
	@NotNull(message = "상품 만료일은 필수입니다")
	LocalDateTime expiredAt,
	@NotNull(message = "가격은 필수입니다")
	Long price,
	@NotNull(message = "판매량은 필수입니다")
	Long soldQuantity,
	@NotNull(message = "총 판매량은 필수입니다")
	Long maxQuantity
) {
	public Product toDomain(Member member) {
		return Product.builder()
			.member(member)
			.title(title)
			.content(content)
			.status(status)
			.expiredAt(expiredAt)
			.price(price)
			.soldQuantity(soldQuantity)
			.maxQuantity(maxQuantity)
			.build();
	}
}
