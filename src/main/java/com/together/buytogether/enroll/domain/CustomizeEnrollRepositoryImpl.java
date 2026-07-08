package com.together.buytogether.enroll.domain;

import static com.together.buytogether.enroll.domain.QEnroll.*;
import static com.together.buytogether.member.domain.QMember.*;
import static com.together.buytogether.product.domain.QProduct.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.query.Param;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.together.buytogether.enroll.dto.response.RecentEnrollInfoDto;

import jakarta.persistence.EntityManager;

public class CustomizeEnrollRepositoryImpl implements CustomizeEnrollRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public CustomizeEnrollRepositoryImpl(EntityManager em) {
		this.jpaQueryFactory = new JPAQueryFactory(em);
	}

	@Override
	public List<RecentEnrollInfoDto> getRecentEnrolls(@Param("memberId") Long memberId) {
		LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
		return jpaQueryFactory
			.select(Projections.constructor(RecentEnrollInfoDto.class,
				enroll.enrollId.as("enrollId"),
				enroll.quantity,
				enroll.createdAt.as("enrollCreatedAt"),
				member.memberId.as("memberId"),
				member.name.as("memberName"),
				member.email.as("memberEmail"),
				product.productId.as("productId"),
				product.price.as("productPrice"),
				product.title.as("productTitle")))
			.from(enroll)
			.join(enroll.member, member)
			.join(enroll.product, product)
			.where(enroll.member.memberId.eq(memberId)
				.and(enroll.createdAt.goe(sixMonthsAgo)))
			.orderBy(enroll.createdAt.desc())
			.fetch();
	}
}
