package com.together.buytogether.comment.feature;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

public class RegisterCommentTest {
    RegisterComment registerComment;

    @BeforeEach
    void setUp() {
        registerComment = new RegisterComment();
    }

    @Test
    @DisplayName("댓글 등록")
    void registerComment() {
        RegisterComment.Request request = new RegisterComment.Request(
                "댓글 내용",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        Long memberId = 1L;
        Long postId = 1L;
        registerComment.request(memberId, postId, request);
    }

    private class RegisterComment {
        public void request(Long memberID, Long postId, Request request) {


        }

        public record Request(
                String content,
                LocalDateTime createAt,
                LocalDateTime updateAt
        ) {
            public Request {
                Assert.hasText(content, "댓글 내용은 필수입니다.");
                Assert.notNull(createAt, "댓글 생성일은 필수입니다.");
                Assert.notNull(updateAt, "댓글 수정일은 필수입니다.");
            }
        }

    }
}
