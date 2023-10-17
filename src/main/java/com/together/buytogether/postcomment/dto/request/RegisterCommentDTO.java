package com.together.buytogether.postcomment.dto.request;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.postcomment.domain.PostComment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;


public record RegisterCommentDTO(
        @NotBlank(message = "댓글 내용은 필수입니다")
        String content,
        @NotNull(message = "댓글 생성일은 필수입니다")
        LocalDateTime createAt,
        @NotNull(message = "댓글 수정일은 필수입니다")
        LocalDateTime updateAt
) {
    public PostComment toDomain(Member member, Post post) {
        return new PostComment(
                member,
                post,
                content,
                createAt,
                updateAt
        );
    }
}