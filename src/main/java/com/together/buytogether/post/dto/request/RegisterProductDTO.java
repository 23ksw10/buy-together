package com.together.buytogether.post.dto.request;

import jakarta.validation.constraints.NotNull;

public record RegisterProductDTO(
	@NotNull(message = "가격은 필수입니다")
	Long price,
	@NotNull(message = "판매량은 필수입니다")
	Long soldQuantity,
	@NotNull(message = "총 판매량은 필수입니다")
	Long maxQuantity
) {
}

