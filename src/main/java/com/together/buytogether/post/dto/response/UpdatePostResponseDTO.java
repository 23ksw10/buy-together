package com.together.buytogether.post.dto.response;

import lombok.Builder;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Builder
public record UpdatePostResponseDTO(
        Long postId,
        String title,
        String content,
        Long joinCount,
        String memberName,
        LocalDateTime updatedAt
) {
    public UpdatePostResponseDTO {
        Assert.notNull(postId, "게시글 ID는 필수 입니다");
        Assert.hasText(title, "제목은 필수 입니다");
        Assert.hasText(content, "내용은 필수 입니다");
        Assert.isTrue(joinCount >= 0, "참여 인원 수는 0 이상이어야 합니다");
        Assert.hasText(memberName, "판매자 이름은 필수 입니다");
        Assert.notNull(updatedAt, "업데이트 시간은 null일 수 없습니다");
    }
}
