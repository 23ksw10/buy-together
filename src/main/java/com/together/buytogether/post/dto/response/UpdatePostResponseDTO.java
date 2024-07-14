package com.together.buytogether.post.dto.response;

import java.time.LocalDateTime;

import org.springframework.util.Assert;

import lombok.Builder;

@Builder
public record UpdatePostResponseDTO(
	Long postId,
	String title,
	String content,
	String memberName,
	LocalDateTime updatedAt
) {
	public UpdatePostResponseDTO {
		Assert.notNull(postId, "게시글 ID는 필수 입니다");
		Assert.hasText(title, "제목은 필수 입니다");
		Assert.hasText(content, "내용은 필수 입니다");
		Assert.hasText(memberName, "판매자 이름은 필수 입니다");
		Assert.notNull(updatedAt, "업데이트 시간은 null일 수 없습니다");
	}
}
