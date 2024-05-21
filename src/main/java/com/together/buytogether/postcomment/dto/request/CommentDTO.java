package com.together.buytogether.postcomment.dto.request;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.postcomment.domain.PostComment;
import jakarta.validation.constraints.NotBlank;

public record CommentDTO(
        @NotBlank(message = "댓글 내용은 필수입니다")
        String content
) {
        public PostComment toDomain(Member member, Post post) {
                return new PostComment(
                        member,
                        post,
                        content
                );
        }
}
