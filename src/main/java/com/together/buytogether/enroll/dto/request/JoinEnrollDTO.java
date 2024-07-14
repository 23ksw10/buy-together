package com.together.buytogether.enroll.dto.request;

import jakarta.validation.constraints.NotNull;

public record JoinEnrollDTO(
	@NotNull(message = "productId는 필수 값입니다")
	Long productId,
	@NotNull(message = "구매 갯수는 필수 값입니다.")
	Long quantity
) {
}
