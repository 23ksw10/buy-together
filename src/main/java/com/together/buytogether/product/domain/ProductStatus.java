package com.together.buytogether.product.domain;

public enum ProductStatus {
	OPEN("OPEN"),
	SOLD_OUT("SOLD_OUT"),
	CLOSED("CLOSED");

	private final String description;

	ProductStatus(String description) {
		this.description = description;
	}
}
