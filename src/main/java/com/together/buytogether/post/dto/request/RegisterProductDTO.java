package com.together.buytogether.post.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterProductDTO(
	@NotBlank(message = "가격은 필수입니다")
	Long price,
	@NotBlank(message = "판매량은 필수입니다")
	Long soldQuantity,
	@NotBlank(message = "총 판매량은 필수입니다")
	Long maxQuantity
) {
}

