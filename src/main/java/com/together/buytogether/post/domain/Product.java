package com.together.buytogether.post.domain;

import org.springframework.util.Assert;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", unique = true)
	Post post;
	@Id
	@Column(name = "product_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productId;
	@Column(name = "price", nullable = false)
	private Long price;
	@Column(name = "join_quantity", nullable = false)
	private Long soldQuantity;
	@Column(name = "max_quantity", nullable = false)
	private Long maxQuantity;
	@Version
	private Long version;

	@Builder
	public Product(
		Long soldQuantity,
		Long maxQuantity,
		Long price,
		Post post
	) {
		validateConstructor(soldQuantity, maxQuantity, price);
		this.soldQuantity = soldQuantity;
		this.maxQuantity = maxQuantity;
		this.price = price;
		this.post = post;
	}

	private static void validateConstructor(Long soldQuantity, Long maxQuantity, Long price) {
		Assert.isTrue(soldQuantity >= 0, "판매량은 0이상이어야 한다");
		Assert.isTrue(maxQuantity >= soldQuantity, "판매량은 최대 판매량을 넘을 수 없다");
		Assert.isTrue(price >= 0, "가격은 0이상이어야 합니다");
	}

	public void increaseSoldQuantity(Long quantity) {
		if (quantity <= 0) {
			throw new CustomException(ErrorCode.INVALID_QUANTITY);
		}
		if (soldQuantity + quantity > maxQuantity) {
			throw new CustomException(ErrorCode.PRODUCT_QUANTITY_EXCEEDED);
		}
		soldQuantity += quantity;
	}

	public void decreaseSoldQuantity(Long quantity) {
		if (soldQuantity <= 0) {
			throw new CustomException(ErrorCode.ENROLL_MEMBER_NOT_BE_NEGATIVE);
		}
		soldQuantity -= quantity;
	}
}
