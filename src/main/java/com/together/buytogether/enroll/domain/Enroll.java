package com.together.buytogether.enroll.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.google.common.annotations.VisibleForTesting;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.product.domain.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "enroll", indexes = {
	@Index(name = "idx_enroll_product_id", columnList = "product_id"),
	@Index(name = "idx_enroll_product_id_created_at", columnList = "product_id, created_at")
})
@NoArgsConstructor
@Comment("구매 참여")
@EntityListeners(AuditingEntityListener.class)
public class Enroll {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "enroll_id")
	private Long enrollId;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	@Column(name = "quantity")
	private Long quantity;
	@CreatedDate
	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Builder
	public Enroll(Member member, Product product, Long quantity) {
		this.member = member;
		this.product = product;
		this.quantity = quantity;
	}

	@VisibleForTesting
	public void setId(Long id) {
		this.enrollId = id;
	}

	@VisibleForTesting
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
