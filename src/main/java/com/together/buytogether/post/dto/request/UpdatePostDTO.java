package com.together.buytogether.post.dto.request;

import com.together.buytogether.post.domain.PostStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UpdatePostDTO(
        @NotBlank(message = "글 제목은 필수입니다")
        String title,
        @NotBlank(message = "글 내용은 필수입니다")
        String content,
        @NotNull(message = "글 상태는 필수입니다")
        PostStatus status,
        @NotNull(message = "글 만료일은 필수입니다")
        LocalDateTime expiredAt,
        @NotNull(message = "최대 구매 참여 인원은 필수입니다")
        Long maxJoinCount) {
}
