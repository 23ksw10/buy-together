package com.together.buytogether.product.domain;

import java.time.LocalDateTime;

import com.together.buytogether.product.dto.request.RegisterProductDTO;

public class RegisterProductDTOFixture {
	private String title = "title";
	private String content = "content";
	private ProductStatus status = ProductStatus.OPEN;
	private LocalDateTime expiredAt = LocalDateTime.now().plusDays(1);
	private Long price = 1000L;
	private Long soldQuantity = 0L;
	private Long maxQuantity = 100L;

	public static RegisterProductDTOFixture aRegisterProductDTO() {
		return new RegisterProductDTOFixture();
	}

	public RegisterProductDTOFixture title(String title) {
		this.title = title;
		return this;
	}

	public RegisterProductDTOFixture content(String content) {
		this.content = content;
		return this;
	}

	public RegisterProductDTOFixture status(ProductStatus status) {
		this.status = status;
		return this;
	}

	public RegisterProductDTOFixture expiredAt(LocalDateTime expiredAt) {
		this.expiredAt = expiredAt;
		return this;
	}

	public RegisterProductDTOFixture price(Long price) {
		this.price = price;
		return this;
	}

	public RegisterProductDTOFixture soldQuantity(Long soldQuantity) {
		this.soldQuantity = soldQuantity;
		return this;
	}

	public RegisterProductDTOFixture maxQuantity(Long maxQuantity) {
		this.maxQuantity = maxQuantity;
		return this;
	}

	public RegisterProductDTO build() {
		return RegisterProductDTO.builder()
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
