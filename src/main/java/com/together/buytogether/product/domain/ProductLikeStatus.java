package com.together.buytogether.product.domain;

public enum ProductLikeStatus {
	OPEN("OPEN"),
	CLOSED("CLOSED");

	private final String description;

	ProductLikeStatus(String description) {
		this.description = description;
	}
}
