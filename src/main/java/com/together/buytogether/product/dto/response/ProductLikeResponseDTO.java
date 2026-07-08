package com.together.buytogether.product.dto.response;

import lombok.Builder;

@Builder
public record ProductLikeResponseDTO(
	Long productId,
	Long memberId,
	boolean isDeleted
) {
}
