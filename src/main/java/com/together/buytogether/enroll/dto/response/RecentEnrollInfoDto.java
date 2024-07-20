package com.together.buytogether.enroll.dto.response;

import java.time.LocalDateTime;

import org.springframework.util.Assert;

public record RecentEnrollInfoDto(
	Long enrollId,
	Long quantity,
	LocalDateTime enrollCreatedAt,
	Long memberId,
	String memberName,
	String memberEmail,
	Long productId,
	Long productPrice,
	Long postId,
	String postTitle
) {
	public RecentEnrollInfoDto {
		Assert.notNull(enrollId, "등록 ID는 필수 값입니다");
		Assert.notNull(memberId, "회원 ID는 필수 값입니다");
		Assert.hasText(memberName, "회원 이름은 필수 값입니다");
		Assert.notNull(postId, "게시글 ID는 필수 값입니다");
		Assert.hasText(postTitle, "게시글 제목은 필수 값입니다");
		Assert.hasText(memberEmail, "판매자 이메일은 필수 값입니다");
		Assert.notNull(enrollCreatedAt, "구매 시간은 필수 값입니다");
		Assert.notNull(productId, "상품 ID는 필수 값입니다");
		Assert.notNull(productPrice, "상품 가격은 필수 값입니다");
		Assert.notNull(quantity, "구매량은 필수 값입니다");
	}
}
