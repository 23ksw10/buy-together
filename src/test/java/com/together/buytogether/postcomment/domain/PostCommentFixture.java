package com.together.buytogether.postcomment.domain;

import com.together.buytogether.member.domain.MemberFixture;
import com.together.buytogether.post.domain.PostFixture;

import java.time.LocalDateTime;

public class PostCommentFixture {
    private MemberFixture memberFixture = MemberFixture.aMember();
    private PostFixture postFixture = PostFixture.aPost();
    private Long commentId = 1L;
    private String content = "댓글 내용";
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public static PostCommentFixture aPostComment() {
        return new PostCommentFixture();
    }

    public PostCommentFixture memberFixture(MemberFixture memberFixture) {
        this.memberFixture = memberFixture;
        return this;
    }

    public PostCommentFixture postFixture(PostFixture postFixture) {
        this.postFixture = postFixture;
        return this;
    }

    public PostCommentFixture commentId(Long commentId) {
        this.commentId = commentId;
        return this;
    }

    public PostCommentFixture content(String content) {
        this.content = content;
        return this;
    }

    public PostCommentFixture createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public PostCommentFixture updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public PostComment build() {
        return new PostComment(
                memberFixture.build(),
                postFixture.build(),
                content,
                createdAt,
                updatedAt
        );
    }
}