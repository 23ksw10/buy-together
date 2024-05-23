package com.together.buytogether.postcomment.domain;

import com.together.buytogether.post.domain.PostFixture;
import com.together.buytogether.post.domain.PostStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PostCommentTest {
    @Test
    @DisplayName("댓글 수정")
    void updateComment() {
        PostComment postComment = PostCommentFixture.aPostComment().build();
        String beforeContent = postComment.getContent();
        LocalDateTime updateDate = LocalDateTime.now();
        postComment.update("댓글 수정", updateDate);
        assertThat(beforeContent).isEqualTo("댓글 내용");
        assertThat(postComment.getContent()).isEqualTo("댓글 수정");
        assertThat(postComment.getUpdatedAt()).isEqualTo(updateDate);
    }

    @Test
    @DisplayName("댓글 수정 - 게시글 상태가 CLOSED일 경우 예외가 발생한다")
    void fail_invalid_status_post_status_updateComment() {
        PostFixture postFixture = PostFixture.aPost().status(PostStatus.CLOSED);
        PostComment postComment = PostCommentFixture.aPostComment().post(postFixture.build()).build();
        LocalDateTime updateDate = LocalDateTime.now();
        assertThatThrownBy(() -> {
            postComment.update("댓글 수정", updateDate);
        })
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("종료된 게시글 댓글은 수정할 수 없습니다.");
    }
}
