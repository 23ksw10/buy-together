package com.together.buytogether.product.domain;

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
@Table(name = "product_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductLike {
	@Id
	@Column(name = "product_like_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productLikeId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	@Column(name = "like_status", nullable = false)
	@Enumerated(EnumType.STRING)
	private ProductLikeStatus productLikeStatus;

	@Builder
	public ProductLike(Member member, Product product, ProductLikeStatus productLikeStatus) {
		this.member = member;
		this.product = product;
		this.productLikeStatus = productLikeStatus;
	}

	public void delete() {
		this.productLikeStatus = ProductLikeStatus.CLOSED;
	}

	public void active() {
		this.productLikeStatus = ProductLikeStatus.OPEN;
	}

	public void changeStatus() {
		if (this.productLikeStatus == ProductLikeStatus.OPEN) {
			this.productLikeStatus = ProductLikeStatus.CLOSED;
		} else {
			this.productLikeStatus = ProductLikeStatus.OPEN;
		}
	}
}
