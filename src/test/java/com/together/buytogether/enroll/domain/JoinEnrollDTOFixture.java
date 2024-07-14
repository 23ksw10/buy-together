package com.together.buytogether.enroll.domain;

import com.together.buytogether.enroll.dto.request.JoinEnrollDTO;

public class JoinEnrollDTOFixture {
	private Long productId = 1L;
	private Long quantity = 1L;

	public static JoinEnrollDTOFixture aJoinEnrollDTOFixture() {
		return new JoinEnrollDTOFixture();
	}

	public JoinEnrollDTOFixture productId(Long productId) {
		this.productId = productId;
		return this;
	}

	public JoinEnrollDTOFixture quantity(Long quantity) {
		this.quantity = quantity;
		return this;
	}

	public JoinEnrollDTO build() {
		return new JoinEnrollDTO(productId, quantity);
	}
}
