package com.together.buytogether.post.domain;

import java.time.LocalDateTime;

import com.together.buytogether.post.dto.request.RegisterPostDTO;

public class RegisterPostDTOFixture {
	private String title = "title";
	private String content = "content";
	private PostStatus status = PostStatus.OPEN;
	private LocalDateTime expiredAt = LocalDateTime.now();

	public static RegisterPostDTOFixture aRegisterPostDTO() {
		return new RegisterPostDTOFixture();
	}

	public RegisterPostDTOFixture title(String title) {
		this.title = title;
		return this;
	}

	public RegisterPostDTOFixture content(String content) {
		this.content = content;
		return this;
	}

	public RegisterPostDTOFixture expiredAt(LocalDateTime expiredAt) {
		this.expiredAt = expiredAt;
		return this;
	}

	public RegisterPostDTOFixture status(PostStatus status) {
		this.status = status;
		return this;
	}

	public RegisterPostDTO build() {
		return RegisterPostDTO.builder()
			.title(title)
			.content(content)
			.status(status)
			.expiredAt(expiredAt)
			.build();
	}
}
