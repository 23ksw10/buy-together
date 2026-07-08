package com.together.buytogether.enroll.dto.response;

import java.time.LocalDateTime;

import org.springframework.util.Assert;

import lombok.Builder;

@Builder
public record JoinEnrollResponseDTO(
	Long enrollId,
	Long memberId,
	String memberName,
	Long productId,
	String productTitle,
	String sellerName,
	LocalDateTime joinedAt
) {
	public JoinEnrollResponseDTO {
		Assert.notNull(enrollId, "등록 ID는 필수 값입니다");
		Assert.notNull(memberId, "회원 ID는 필수 값입니다");
		Assert.hasText(memberName, "회원 이름은 필수 값입니다");
		Assert.notNull(productId, "상품 ID는 필수 값입니다");
		Assert.hasText(productTitle, "상품 제목은 필수 값입니다");
		Assert.hasText(sellerName, "판매자 이름은 필수 값입니다");
		Assert.notNull(joinedAt, "구매 시간은 필수 값입니다");
	}
}
