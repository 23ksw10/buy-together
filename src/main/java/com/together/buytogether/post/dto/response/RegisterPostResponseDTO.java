package com.together.buytogether.post.dto.response;

import java.time.LocalDateTime;

import org.springframework.util.Assert;

import lombok.Builder;

@Builder
public record RegisterPostResponseDTO(
	Long postId,
	String title,
	String content,
	String name,
	LocalDateTime createdAt
) {
	public RegisterPostResponseDTO {
		Assert.notNull(postId, "게시글 ID는 필수입니다");
		Assert.hasText(title, "제목은 필수입니다");
		Assert.hasText(content, "내용은 필수입니다");
		Assert.hasText(name, "판매자 이름은 필수 입니다");
		Assert.notNull(createdAt, "생성 시간은 필수입니다");
	}
}
