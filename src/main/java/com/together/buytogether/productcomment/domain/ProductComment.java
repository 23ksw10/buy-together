package com.together.buytogether.productcomment.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.Assert;

import com.google.common.annotations.VisibleForTesting;
import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.product.domain.Product;
import com.together.buytogether.product.domain.ProductStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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

@Entity
@Getter
@Table(name = "comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Comment("댓글")
@EntityListeners(AuditingEntityListener.class)
public class ProductComment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
	@Comment("댓글 ID")
	private Long commentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	@Column(name = "content", nullable = false)
	@Comment("댓글 내용")
	private String content;

	@CreatedDate
	@Column(name = "created_at", nullable = false)
	@Comment("댓글 생성일")
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "update_at", nullable = false)
	@Comment("댓글 수정일")
	private LocalDateTime updatedAt;

	@Builder
	public ProductComment(Member member, Product product, String content) {
		validateConstructor(member, product, content);
		this.member = member;
		this.product = product;
		this.content = content;
	}

	private static void validateConstructor(Member member, Product product, String content) {
		Assert.notNull(member, "댓글 작성자는 필수입니다.");
		Assert.notNull(product, "댓글이 달린 상품은 필수입니다.");
		Assert.hasText(content, "댓글 내용은 필수입니다.");
	}

	public boolean checkOwner(Long memberId) {
		return this.member.getMemberId().equals(memberId);
	}

	public void checkProductStatus() {
		if (this.product.getStatus().equals(ProductStatus.CLOSED)) {
			throw new CustomException(ErrorCode.PRODUCT_CLOSED);
		}
	}

	public void update(String content, LocalDateTime now) {
		checkProductStatus();
		this.content = content;
		this.updatedAt = now;
	}

	@VisibleForTesting
	public void setCommentId(Long id) {
		this.commentId = id;
	}

	@VisibleForTesting
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
