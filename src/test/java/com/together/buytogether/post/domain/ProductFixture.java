package com.together.buytogether.post.domain;

import static com.together.buytogether.post.domain.PostFixture.*;

public class ProductFixture {
	private Post post = aPost().build();

	private Long price = 1000L;
	private Long soldQuantity = 0L;
	private Long maxQuantity = 100L;

	public static ProductFixture aProduct() {
		return new ProductFixture();
	}

	public ProductFixture maxQuantity(Long maxQuantity) {
		this.maxQuantity = maxQuantity;
		return this;
	}

	public ProductFixture price(Long price) {
		this.price = price;
		return this;
	}

	public ProductFixture soldQuantity(Long soldQuantity) {
		this.soldQuantity = soldQuantity;
		return this;
	}

	public ProductFixture post(Post post) {
		this.post = post;
		return this;
	}

	public Product build() {
		return new Product(
			soldQuantity,
			maxQuantity,
			price,
			post
		);
	}
}
