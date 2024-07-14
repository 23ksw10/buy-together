package com.together.buytogether.enroll.domain;

import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberFixture;
import com.together.buytogether.post.domain.Product;
import com.together.buytogether.post.domain.ProductFixture;

public class EnrollFixture {
	private Member member = MemberFixture.aMember().build();
	private Product product = ProductFixture.aProduct().build();
	private Long quantity = 1L;

	public static EnrollFixture aEnroll() {
		return new EnrollFixture();
	}

	public EnrollFixture member(Member member) {
		this.member = member;
		return this;
	}

	public EnrollFixture product(Product product) {
		this.product = product;
		return this;
	}

	public EnrollFixture quantity(Long quantity) {
		this.quantity = quantity;
		return this;
	}

	public Enroll build() {
		return new Enroll(member, product, quantity);
	}
}
