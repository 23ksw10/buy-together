package com.together.buytogether.postcomment.dto.response;

import lombok.Builder;
import org.springframework.util.Assert;

@Builder
public record UpdateCommentResponseDTO(
        Long commentId,
        Long postId,
        String memberName,
        String currentContent,
        String updatedAt) {

    public UpdateCommentResponseDTO {
        Assert.notNull(commentId, "댓글 번호는 필수 값입니다");
        Assert.notNull(postId, "글 번호는 필수 값입니다");
        Assert.hasText(memberName, "회원 이름은 필수 값입니다");
        Assert.hasText(currentContent, "댓글 내용은 필수 값입니다");
        Assert.hasText(updatedAt, "댓글 수정일은 필수 값입니다");
    }
}
