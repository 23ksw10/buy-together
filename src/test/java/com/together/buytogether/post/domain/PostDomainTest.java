package com.together.buytogether.post.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.together.buytogether.common.error.CustomException;

public class PostDomainTest {
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
	@DisplayName("게시글 수정 - 게시글 상태가 CLOSED 경우 예외가 발생한다")
	void throwExceptionWhenUpdateClosedPost() {
		Post post = PostFixture.aPost().status(PostStatus.CLOSED).build();
		assertThatThrownBy(() -> {
			post.update("newTitle",
				"newContent",
				PostStatus.OPEN, LocalDateTime.now().plusDays(2));
		})
			.isInstanceOf(CustomException.class)
			.hasMessage("이미 마감된 게시글입니다");

	}
}
