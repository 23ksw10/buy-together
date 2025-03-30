package com.together.buytogether.post.dto.request;

import jakarta.validation.constraints.NotNull;

public record LikeRequestDTO(
	@NotNull
	Long memberId,
	@NotNull
	Long postId
) {
}
