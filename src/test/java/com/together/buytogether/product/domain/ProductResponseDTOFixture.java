package com.together.buytogether.product.domain;

import java.time.LocalDateTime;

import com.together.buytogether.product.dto.response.ProductResponseDTO;

public class ProductResponseDTOFixture {
	private String memberName = "작성자 이름";
	private Long productId = 1L;
	private String title = "제목";
	private String content = "내용";
	private ProductStatus status = ProductStatus.OPEN;
	private LocalDateTime expiredAt = LocalDateTime.now().plusDays(1);
	private Long price = 1000L;
	private Long soldQuantity = 0L;
	private Long maxQuantity = 100L;

	public static ProductResponseDTOFixture aProductResponseDTO() {
		return new ProductResponseDTOFixture();
	}

	public ProductResponseDTOFixture memberName(String memberName) {
		this.memberName = memberName;
		return this;
	}

	public ProductResponseDTOFixture productId(Long productId) {
		this.productId = productId;
		return this;
	}

	public ProductResponseDTOFixture title(String title) {
		this.title = title;
		return this;
	}

	public ProductResponseDTOFixture content(String content) {
		this.content = content;
		return this;
	}

	public ProductResponseDTOFixture status(ProductStatus status) {
		this.status = status;
		return this;
	}

	public ProductResponseDTOFixture expiredAt(LocalDateTime expiredAt) {
		this.expiredAt = expiredAt;
		return this;
	}

	public ProductResponseDTOFixture price(Long price) {
		this.price = price;
		return this;
	}

	public ProductResponseDTOFixture soldQuantity(Long soldQuantity) {
		this.soldQuantity = soldQuantity;
		return this;
	}

	public ProductResponseDTOFixture maxQuantity(Long maxQuantity) {
		this.maxQuantity = maxQuantity;
		return this;
	}

	public ProductResponseDTO build() {
		return ProductResponseDTO.builder()
			.memberName(memberName)
			.productId(productId)
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
