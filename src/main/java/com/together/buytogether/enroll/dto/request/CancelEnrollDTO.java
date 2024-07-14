package com.together.buytogether.enroll.dto.request;

import jakarta.validation.constraints.NotNull;

public record CancelEnrollDTO(
	@NotNull(message = "productId는 필수 값입니다")
	Long productId
) {
}
