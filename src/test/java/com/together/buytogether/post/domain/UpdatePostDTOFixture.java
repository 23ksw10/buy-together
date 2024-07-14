package com.together.buytogether.post.domain;

import java.time.LocalDateTime;

import com.together.buytogether.post.dto.request.UpdatePostDTO;

public class UpdatePostDTOFixture {
	private String title = "title";
	private String content = "content";
	private PostStatus status = PostStatus.CLOSED;
	private LocalDateTime expiredAt = LocalDateTime.now();

	public static UpdatePostDTOFixture aUpdatePostDTO() {
		return new UpdatePostDTOFixture();
	}

	public UpdatePostDTOFixture title(String title) {
		this.title = title;
		return this;
	}

	public UpdatePostDTOFixture content(String content) {
		this.content = content;
		return this;
	}

	public UpdatePostDTOFixture expiredAt(LocalDateTime expiredAt) {
		this.expiredAt = expiredAt;
		return this;
	}

	public UpdatePostDTOFixture status(PostStatus status) {
		this.status = status;
		return this;
	}

	public UpdatePostDTO build() {
		return UpdatePostDTO.builder()
			.title(title)
			.content(content)
			.status(status)
			.expiredAt(expiredAt)
			.build();
	}
}
