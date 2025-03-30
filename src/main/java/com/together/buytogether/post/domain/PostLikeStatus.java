package com.together.buytogether.post.domain;

public enum PostLikeStatus {
	OPEN("OPEN"),
	CLOSED("CLOSED");
	private final String description;

	PostLikeStatus(String description) {
		this.description = description;
	}
}
