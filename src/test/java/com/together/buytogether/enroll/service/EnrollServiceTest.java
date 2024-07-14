package com.together.buytogether.enroll.service;

import static com.together.buytogether.enroll.domain.JoinEnrollDTOFixture.*;
import static com.together.buytogether.member.domain.MemberFixture.*;
import static com.together.buytogether.post.domain.PostFixture.*;
import static com.together.buytogether.post.domain.ProductFixture.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.service.CommonMemberService;
import com.together.buytogether.common.service.CommonProductService;
import com.together.buytogether.enroll.domain.Enroll;
import com.together.buytogether.enroll.domain.EnrollRepository;
import com.together.buytogether.enroll.dto.request.JoinEnrollDTO;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.Product;

@ExtendWith(MockitoExtension.class)
public class EnrollServiceTest {
	Long productId;
	Member member;
	Post post;
	@Mock
	private CommonMemberService commonMemberService;

	Long memberId;
	@Mock
	private CommonProductService commonProductService;
	@Mock
	private EnrollRepository enrollRepository;
	@InjectMocks
	private EnrollService enrollService;

	@BeforeEach
	void setUp() {
		member = aMember().memberId(1L).build();
		member.setId(1L);
		post = aPost().member(member).build();
		post.setPostId(1L);
		memberId = 1L;
		productId = 1L;
	}

	@Test
	@DisplayName("구매 참여 조건이 충족될 때 구매에 참여할 수 있다")
	void joinBuyingSuccess() {
		//given
		Long soldQuantity = 1L;
		Product product = aProduct().post(post).soldQuantity(soldQuantity).build();
		Enroll existingEnroll = Enroll.builder()
			.member(member)
			.product(product)
			.build();
		existingEnroll.setId(1L);
		existingEnroll.setCreatedAt(LocalDateTime.now());
		JoinEnrollDTO joinEnrollDTO = aJoinEnrollDTOFixture().build();
		given(commonMemberService.getMember(memberId)).willReturn(member);
		given(commonProductService.getProduct(productId)).willReturn(product);
		given(enrollRepository.findByMemberIdAndProductId(memberId, productId)).willReturn(Optional.empty());
		given(enrollRepository.save(any(Enroll.class))).willReturn(existingEnroll);

		//when
		enrollService.joinBuying(memberId, joinEnrollDTO);

		//then
		then(enrollRepository).should().save(any(Enroll.class));
	}

	@Test
	@DisplayName("이미 구매에 참여한 경우 참여할 수 없다")
	void alreadyEnrolledJoinBuyingFail() {
		//given
		Product product = aProduct().post(post).build();
		Enroll existingEnroll = Enroll.builder()
			.member(member)
			.product(product)
			.build();
		given(commonMemberService.getMember(memberId)).willReturn(member);
		given(commonProductService.getProduct(productId)).willReturn(product);
		given(enrollRepository.findByMemberIdAndProductId(memberId, productId)).willReturn(Optional.of(existingEnroll));
		JoinEnrollDTO joinEnrollDTO = aJoinEnrollDTOFixture().build();

		//when
		assertThrows(CustomException.class, () -> enrollService.joinBuying(memberId, joinEnrollDTO));

		//then
		then(enrollRepository).should(never()).save(any(Enroll.class));

	}

	@Test
	@DisplayName("구매 참여를 취소할 수 있다")
	void cancelBuyingSuccess() {
		//given
		Long soldQuantity = 1L;
		Long enrollId = 1L;
		Long buyQuantity = 1L;
		Product product = aProduct().post(post).soldQuantity(soldQuantity).build();
		Enroll existingEnroll = Enroll.builder()
			.member(member)
			.product(product)
			.quantity(buyQuantity)
			.build();
		given(enrollRepository.getEnroll(enrollId)).willReturn(existingEnroll);

		//when
		enrollService.cancelBuying(memberId, enrollId);

		//then
		then(enrollRepository).should().delete(existingEnroll);
		assertEquals(product.getSoldQuantity(), 0L);
	}

	@Test
	@DisplayName("구매 참여한 적이 없을 경우 취소할 수 없다")
	void nonExistingEnrollCancelBuyingFail() {
		//given
		Long enrollId = 1L;
		given(enrollRepository.getEnroll(enrollId)).willThrow(CustomException.class);

		//when
		assertThrows(CustomException.class, () -> enrollService.cancelBuying(memberId, enrollId));

		//then
		then(enrollRepository).should(never()).save(any(Enroll.class));
	}

}
