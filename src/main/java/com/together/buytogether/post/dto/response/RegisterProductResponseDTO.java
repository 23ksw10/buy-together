package com.together.buytogether.post.dto.response;

import org.springframework.util.Assert;

import lombok.Builder;

public record RegisterProductResponseDTO(
	Long postId,
	Long productId,
	Long price
) {
	@Builder
	public RegisterProductResponseDTO {
		Assert.notNull(postId, "postId는 필수 값입니다");
		Assert.notNull(productId, "productId는 필수 값입니다");
		Assert.isTrue(price >= 0, "price는 0이상이어야 합니다");

	}
}
