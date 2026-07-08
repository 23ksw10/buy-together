package com.together.buytogether.product.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.google.common.annotations.VisibleForTesting;
import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import com.together.buytogether.member.domain.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	@Comment("상품 아이디")
	private Long productId;

	@Comment("회원 ID")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Column(name = "title", nullable = false)
	@Comment("상품 제목")
	private String title;

	@Column(name = "content", nullable = false)
	@Comment("상품 설명")
	private String content;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	@Comment("상품 상태")
	private ProductStatus status;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@Column(name = "expired_at", nullable = false)
	@Comment("상품 만료일")
	private LocalDateTime expiredAt;

	@Column(name = "price", nullable = false)
	@Comment("상품 가격")
	private Long price;

	@Column(name = "sold_quantity", nullable = false)
	@Comment("판매 수량")
	private Long soldQuantity;

	@Column(name = "max_quantity", nullable = false)
	@Comment("최대 판매 수량")
	private Long maxQuantity;

	@Builder
	public Product(
		Member member,
		String title,
		String content,
		ProductStatus status,
		LocalDateTime expiredAt,
		Long price,
		Long soldQuantity,
		Long maxQuantity
	) {
		validateConstructor(member, title, content, status, expiredAt, price, soldQuantity, maxQuantity);
		this.member = member;
		this.title = title;
		this.content = content;
		this.status = status;
		this.expiredAt = expiredAt;
		this.price = price;
		this.soldQuantity = soldQuantity;
		this.maxQuantity = maxQuantity;
	}

	private static void validateConstructor(
		Member member,
		String title,
		String content,
		ProductStatus status,
		LocalDateTime expiredAt,
		Long price,
		Long soldQuantity,
		Long maxQuantity) {
		Assert.notNull(member, "회원 정보는 필수입니다");
		Assert.hasText(title, "상품 제목은 필수입니다");
		Assert.hasText(content, "상품 설명은 필수입니다");
		Assert.notNull(status, "상품 상태는 필수입니다");
		Assert.notNull(expiredAt, "상품 만료일은 필수입니다");
		Assert.notNull(price, "가격은 필수입니다");
		Assert.notNull(soldQuantity, "판매량은 필수입니다");
		Assert.notNull(maxQuantity, "최대 판매량은 필수입니다");
		Assert.isTrue(price >= 0, "가격은 0이상이어야 합니다");
		Assert.isTrue(soldQuantity >= 0, "판매량은 0이상이어야 한다");
		Assert.isTrue(maxQuantity >= soldQuantity, "판매량은 최대 판매량을 넘을 수 없다");
	}

	public void checkOwner(Long memberId) {
		if (!memberId.equals(this.member.getMemberId())) {
			throw new CustomException(ErrorCode.IS_NOT_OWNER);
		}
	}

	public void update(
		String title,
		String content,
		ProductStatus status,
		LocalDateTime expiredAt,
		Long price,
		Long maxQuantity) {
		validateUpdate(title, content, status, expiredAt, price, maxQuantity);
		this.title = title;
		this.content = content;
		this.status = status;
		this.expiredAt = expiredAt;
		this.price = price;
		this.maxQuantity = maxQuantity;
		if (this.status == ProductStatus.OPEN && this.soldQuantity.equals(this.maxQuantity)) {
			this.status = ProductStatus.SOLD_OUT;
		}
	}

	private void validateUpdate(
		String title,
		String content,
		ProductStatus status,
		LocalDateTime expiredAt,
		Long price,
		Long maxQuantity) {
		validateStatusWhenUpdate();
		Assert.hasText(title, "상품 제목은 필수입니다");
		Assert.hasText(content, "상품 설명은 필수입니다");
		Assert.notNull(status, "상품 상태는 필수입니다");
		Assert.notNull(expiredAt, "상품 만료일은 필수입니다");
		Assert.notNull(price, "가격은 필수입니다");
		Assert.notNull(maxQuantity, "최대 판매량은 필수입니다");
		Assert.isTrue(price >= 0, "가격은 0이상이어야 합니다");
		Assert.isTrue(maxQuantity >= soldQuantity, "최대 판매량은 현재 판매량보다 작을 수 없습니다");
	}

	private void validateStatusWhenUpdate() {
		if (status == ProductStatus.CLOSED) {
			throw new CustomException(ErrorCode.PRODUCT_CLOSED);
		}
	}

	public void increaseSoldQuantity(Long quantity) {
		validateJoinableStatus();
		if (quantity <= 0) {
			throw new CustomException(ErrorCode.INVALID_QUANTITY);
		}
		if (soldQuantity + quantity > maxQuantity) {
			throw new CustomException(ErrorCode.PRODUCT_QUANTITY_EXCEEDED);
		}
		soldQuantity += quantity;
		if (soldQuantity.equals(maxQuantity)) {
			status = ProductStatus.SOLD_OUT;
		}
	}

	private void validateJoinableStatus() {
		if (status == ProductStatus.CLOSED) {
			throw new CustomException(ErrorCode.PRODUCT_CLOSED);
		}
		if (status == ProductStatus.SOLD_OUT) {
			throw new CustomException(ErrorCode.PRODUCT_SOLD_OUT);
		}
	}

	public void decreaseSoldQuantity(Long quantity) {
		if (quantity <= 0) {
			throw new CustomException(ErrorCode.INVALID_QUANTITY);
		}
		if (soldQuantity - quantity < 0) {
			throw new CustomException(ErrorCode.ENROLL_MEMBER_NOT_BE_NEGATIVE);
		}
		soldQuantity -= quantity;
		if (status == ProductStatus.SOLD_OUT && soldQuantity < maxQuantity) {
			status = ProductStatus.OPEN;
		}
	}

	public void deleteProduct() {
		if (status == ProductStatus.CLOSED) {
			throw new CustomException(ErrorCode.PRODUCT_CLOSED);
		}
		status = ProductStatus.CLOSED;
	}

	@VisibleForTesting
	public void setProductId(Long productId) {
		this.productId = productId;
	}
}
