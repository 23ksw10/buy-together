package com.together.buytogether.product.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.together.buytogether.common.error.CustomException;

public class ProductDomainTest {
	@Test
	@DisplayName("상품 수정")
	void updateProduct() {
		Product product = ProductFixture.aProduct().build();
		String beforeTitle = product.getTitle();
		String beforeContent = product.getContent();
		product.update("newTitle", "newContent", ProductStatus.OPEN, LocalDateTime.now().plusDays(2), 2000L, 200L);
		assertThat(beforeTitle).isEqualTo("title");
		assertThat(product.getTitle()).isEqualTo("newTitle");
		assertThat(beforeContent).isEqualTo("content");
		assertThat(product.getContent()).isEqualTo("newContent");
		assertThat(product.getPrice()).isEqualTo(2000L);
		assertThat(product.getMaxQuantity()).isEqualTo(200L);
	}

	@Test
	@DisplayName("상품 수정 - 상품 상태가 CLOSED 경우 예외가 발생한다")
	void throwExceptionWhenUpdateClosedProduct() {
		Product product = ProductFixture.aProduct().status(ProductStatus.CLOSED).build();
		assertThatThrownBy(() -> {
			product.update("newTitle",
				"newContent",
				ProductStatus.OPEN,
				LocalDateTime.now().plusDays(2),
				2000L,
				200L);
		})
			.isInstanceOf(CustomException.class)
			.hasMessage("이미 마감된 상품입니다");

	}

	@Test
	@DisplayName("판매 수량이 최대 수량에 도달하면 SOLD_OUT 상태가 된다")
	void markSoldOutWhenSoldQuantityReachesMaxQuantity() {
		Product product = ProductFixture.aProduct()
			.soldQuantity(9L)
			.maxQuantity(10L)
			.build();

		product.increaseSoldQuantity(1L);

		assertThat(product.getSoldQuantity()).isEqualTo(10L);
		assertThat(product.getStatus()).isEqualTo(ProductStatus.SOLD_OUT);
	}

	@Test
	@DisplayName("판매 수량이 최대 수량을 초과하면 예외가 발생한다")
	void throwExceptionWhenSoldQuantityExceedsMaxQuantity() {
		Product product = ProductFixture.aProduct()
			.soldQuantity(9L)
			.maxQuantity(10L)
			.build();

		assertThatThrownBy(() -> product.increaseSoldQuantity(2L))
			.isInstanceOf(CustomException.class)
			.hasMessage("구매 한도가 초과 되었습니다");

		assertThat(product.getSoldQuantity()).isEqualTo(9L);
		assertThat(product.getStatus()).isEqualTo(ProductStatus.OPEN);
	}

	@Test
	@DisplayName("SOLD_OUT 상품은 추가 구매에 참여할 수 없다")
	void throwExceptionWhenIncreaseSoldQuantityOnSoldOutProduct() {
		Product product = ProductFixture.aProduct()
			.status(ProductStatus.SOLD_OUT)
			.soldQuantity(10L)
			.maxQuantity(10L)
			.build();

		assertThatThrownBy(() -> product.increaseSoldQuantity(1L))
			.isInstanceOf(CustomException.class)
			.hasMessage("상품 재고가 모두 소진되었습니다");
	}

	@Test
	@DisplayName("SOLD_OUT 상품의 구매가 취소되면 OPEN 상태가 된다")
	void reopenSoldOutProductWhenSoldQuantityDecreases() {
		Product product = ProductFixture.aProduct()
			.status(ProductStatus.SOLD_OUT)
			.soldQuantity(10L)
			.maxQuantity(10L)
			.build();

		product.decreaseSoldQuantity(1L);

		assertThat(product.getSoldQuantity()).isEqualTo(9L);
		assertThat(product.getStatus()).isEqualTo(ProductStatus.OPEN);
	}
}
