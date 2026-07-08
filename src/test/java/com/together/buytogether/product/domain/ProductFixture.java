package com.together.buytogether.product.domain;

import java.time.LocalDateTime;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberFixture;

public class ProductFixture {
	private Member member = MemberFixture.aMember().build();
	private String title = "title";
	private String content = "content";
	private ProductStatus status = ProductStatus.OPEN;
	private LocalDateTime expiredAt = LocalDateTime.now().plusDays(1);
	private Long price = 1000L;
	private Long soldQuantity = 0L;
	private Long maxQuantity = 100L;

	public static ProductFixture aProduct() {
		return new ProductFixture();
	}

	public ProductFixture member(Member member) {
		this.member = member;
		return this;
	}

	public ProductFixture title(String title) {
		this.title = title;
		return this;
	}

	public ProductFixture content(String content) {
		this.content = content;
		return this;
	}

	public ProductFixture status(ProductStatus status) {
		this.status = status;
		return this;
	}

	public ProductFixture expiredAt(LocalDateTime expiredAt) {
		this.expiredAt = expiredAt;
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

	public ProductFixture maxQuantity(Long maxQuantity) {
		this.maxQuantity = maxQuantity;
		return this;
	}

	public ProductFixture setMemberId(Long memberId) {
		this.member.setId(memberId);
		return this;
	}

	public Product build() {
		return Product.builder()
			.member(member)
			.title(title)
			.content(content)
			.status(status)
			.expiredAt(expiredAt)
			.price(price)
			.soldQuantity(soldQuantity)
			.maxQuantity(maxQuantity)
			.build();
	}
}
