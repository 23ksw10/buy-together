package com.together.buytogether.product.domain;

import java.time.LocalDateTime;

import com.together.buytogether.product.dto.request.UpdateProductDTO;

public class UpdateProductDTOFixture {
	private String title = "title";
	private String content = "content";
	private ProductStatus status = ProductStatus.OPEN;
	private LocalDateTime expiredAt = LocalDateTime.now().plusDays(1);
	private Long price = 1000L;
	private Long maxQuantity = 100L;

	public static UpdateProductDTOFixture aUpdateProductDTO() {
		return new UpdateProductDTOFixture();
	}

	public UpdateProductDTOFixture title(String title) {
		this.title = title;
		return this;
	}

	public UpdateProductDTOFixture content(String content) {
		this.content = content;
		return this;
	}

	public UpdateProductDTOFixture status(ProductStatus status) {
		this.status = status;
		return this;
	}

	public UpdateProductDTOFixture expiredAt(LocalDateTime expiredAt) {
		this.expiredAt = expiredAt;
		return this;
	}

	public UpdateProductDTOFixture price(Long price) {
		this.price = price;
		return this;
	}

	public UpdateProductDTOFixture maxQuantity(Long maxQuantity) {
		this.maxQuantity = maxQuantity;
		return this;
	}

	public UpdateProductDTO build() {
		return UpdateProductDTO.builder()
			.title(title)
			.content(content)
			.status(status)
			.expiredAt(expiredAt)
			.price(price)
			.maxQuantity(maxQuantity)
			.build();
	}
}
