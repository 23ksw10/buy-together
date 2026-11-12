package com.together.buytogether.enroll.service;

import static com.together.buytogether.common.lock.LockNumberConstants.LOCK_WAIT_MILLI_SECOND;
import static com.together.buytogether.enroll.domain.JoinEnrollDTOFixture.*;
import static com.together.buytogether.member.domain.MemberFixture.*;
import static com.together.buytogether.product.domain.ProductFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.mockito.Mockito.doAnswer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.jdbc.core.JdbcTemplate;

import com.together.buytogether.common.service.CommonMemberService;
import com.together.buytogether.enroll.dto.request.JoinEnrollDTO;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.product.domain.ProductRepository;

import jakarta.persistence.EntityManager;

@SpringBootTest
class EnrollTransactionTimeoutTest {

	@SpyBean
	CommonMemberService commonMemberService;
	@Autowired
	EnrollFacade enrollFacade;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	EntityManager entityManager;

	private final List<Long> memberIds = new ArrayList<>();
	private Long productId;

	@AfterEach
	void cleanup() {
		jdbcTemplate.update("DELETE FROM enroll WHERE product_id = ?", productId);
		productRepository.deleteById(productId);
		memberRepository.deleteAllByIdInBatch(memberIds);
	}

	@Test
	@DisplayName("참여 트랜잭션이 락 대기 시간을 넘기면 롤백되고, 같은 상품의 다음 참여는 정상 처리된다")
	void joinTransactionTimesOutAndRollsBack() {
		String emailPrefix = "tx-timeout-" + UUID.randomUUID();
		Member seller = saveMember(emailPrefix + "-seller");
		Member slowMember = saveMember(emailPrefix + "-slow");
		Member nextMember = saveMember(emailPrefix + "-next");
		productId = productRepository.save(aProduct().member(seller).build()).getProductId();
		JoinEnrollDTO request = aJoinEnrollDTOFixture().productId(productId).quantity(1L).build();

		// 느린 회원의 참여에서만, 트랜잭션 안에서 락 대기 시간보다 오래 걸리는 쿼리를 실행한다
		long sleepSeconds = LOCK_WAIT_MILLI_SECOND / 1000 + 1;
		doAnswer(invocation -> {
			entityManager.createNativeQuery("SELECT SLEEP(?1)").setParameter(1, sleepSeconds).getSingleResult();
			return invocation.callRealMethod();
		}).when(commonMemberService).getMember(slowMember.getMemberId());

		// timeout이 나면 HikariCP가 커넥션을 폐기해 롤백 실패 예외가 원래 예외를 가리므로,
		// 예외 종류 대신 빨리 끝났는지와 데이터가 반영되지 않았는지를 검증한다
		long startedAt = System.currentTimeMillis();
		assertThatException().isThrownBy(() -> enrollFacade.joinBuying(slowMember.getMemberId(), request));
		assertThat(System.currentTimeMillis() - startedAt).isLessThan(sleepSeconds * 1000);

		enrollFacade.joinBuying(nextMember.getMemberId(), request);

		assertThat(productRepository.getByProductId(productId).getSoldQuantity()).isEqualTo(1L);
	}

	private Member saveMember(String emailLocalPart) {
		Member member = memberRepository.save(aMember().email(emailLocalPart + "@test.com").build());
		memberIds.add(member.getMemberId());
		return member;
	}
}
