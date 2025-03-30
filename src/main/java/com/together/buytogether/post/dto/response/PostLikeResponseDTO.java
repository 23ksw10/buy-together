package com.together.buytogether.post.dto.response;

import lombok.Builder;

@Builder
public record PostLikeResponseDTO(
	Long postId,
	Long memberId,
	boolean isDeleted
) {
}
