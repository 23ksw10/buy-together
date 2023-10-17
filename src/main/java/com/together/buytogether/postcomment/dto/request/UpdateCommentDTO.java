package com.together.buytogether.postcomment.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateCommentDTO(
        @NotBlank(message = "댓글 내용은 필수입니다")
        String content
) {
}
