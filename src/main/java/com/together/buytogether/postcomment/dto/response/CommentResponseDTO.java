package com.together.buytogether.postcomment.dto.response;

import lombok.Builder;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Builder
public record CommentResponseDTO(
        Long commentId,
        Long postId,
        String memberName,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
    public CommentResponseDTO {
        Assert.notNull(commentId, "댓글 번호는 필수 값입니다");
        Assert.notNull(postId, "글 번호는 필수 값입니다");
        Assert.hasText(memberName, "회원 이름은 필수 값입니다");
        Assert.hasText(content, "댓글 내용은 필수 값입니다");
        Assert.notNull(createdAt, "댓글 작성일은 필수 값입니다");
        Assert.notNull(updatedAt, "댓글 수정일은 필수 값입니다");
    }

}
