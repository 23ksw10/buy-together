package com.together.buytogether.post.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class PostTest {
    @Test
    @DisplayName("게시글 수정")
    void updatePost() {
        Post post = PostFixture.aPost().build();
        String beforeTitle = post.getTitle();
        String beforeContent = post.getContent();
        post.update("newTitle", "newContent", PostStatus.OPEN, LocalDateTime.now().plusDays(2));
        assertThat(beforeTitle).isEqualTo("title");
        assertThat(post.getTitle()).isEqualTo("newTitle");
        assertThat(beforeContent).isEqualTo("content");
        assertThat(post.getContent()).isEqualTo("newContent");
    }


    @Test
    @DisplayName("게시글 수정 - 게시글 상태가 CLOSED일 경우 예외가 발생한다")
    void fail_invalid_status_updatePost() {
        Post post = PostFixture.aPost().status(PostStatus.CLOSED).build();
        assertThatThrownBy(() -> {
            post.update("newTitle", "newContent", PostStatus.OPEN, LocalDateTime.now().plusDays(2));
        })
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("종료된 게시글은 수정할 수 없습니다");

    }
}
