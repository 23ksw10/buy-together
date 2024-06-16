package com.together.buytogether.postcomment.domain;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.post.domain.PostFixture;
import com.together.buytogether.post.domain.PostStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PostCommentDomainTest {
    private String updateCommentContent;

    @BeforeEach
    void setUp() {
        updateCommentContent = "Updated Content";
    }

    @Test
    @DisplayName("댓글 수정")
    void updateComment() {
        PostComment postComment = PostCommentFixture.aPostComment().build();
        LocalDateTime updateDate = LocalDateTime.now();
        postComment.update(updateCommentContent, updateDate);
        assertThat(postComment.getContent()).isEqualTo(updateCommentContent);
        assertThat(postComment.getUpdatedAt()).isEqualTo(updateDate);
    }

    @Test
    @DisplayName("댓글 수정 - 게시글 상태가 CLOSED 경우 예외가 발생한다")
    void throwExceptionWhenUpdateCommentOnClosedPost() {
        PostFixture postFixture = PostFixture.aPost().status(PostStatus.CLOSED);
        PostComment postComment = PostCommentFixture.aPostComment().post(postFixture.build()).build();
        LocalDateTime updateDate = LocalDateTime.now();
        assertThatThrownBy(() -> {
            postComment.update(updateCommentContent, updateDate);
        })
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 마감된 게시글입니다");
    }
}
