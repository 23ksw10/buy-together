package com.together.buytogether.post.dto.request;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;


public record RegisterPostDTO(
        @NotBlank(message = "글 제목은 필수입니다")
        String title,
        @NotBlank(message = "글 내용은 필수입니다")

        String content,
        @NotNull(message = "글 상태는 필수입니다")

        PostStatus status,
        @NotNull(message = "글 만료일은 필수입니다")

        LocalDateTime expiredAt) {
    public Post toDomain(Member member) {
        return new Post(
                member,
                title,
                content,
                status,
                expiredAt
        );
    }
}
