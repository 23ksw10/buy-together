package com.together.buytogether.product.dto.request;

import jakarta.validation.constraints.NotNull;

public record LikeRequestDTO(
	@NotNull
	Long memberId,
	@NotNull
	Long productId
) {
}
