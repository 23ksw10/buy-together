package com.together.buytogether.enroll.service;

import static com.together.buytogether.enroll.domain.JoinEnrollDTOFixture.*;
import static com.together.buytogether.member.domain.MemberFixture.*;
import static com.together.buytogether.post.domain.PostFixture.*;
import static com.together.buytogether.post.domain.ProductFixture.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.service.CommonMemberService;
import com.together.buytogether.enroll.domain.Enroll;
import com.together.buytogether.enroll.domain.EnrollRepository;
import com.together.buytogether.enroll.dto.request.CancelEnrollDTO;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostRepository;
import com.together.buytogether.post.domain.Product;
import com.together.buytogether.post.domain.ProductRepository;

@SpringBootTest
public class EnrollConcurrencyTest {

	@Autowired
	EnrollService enrollService;
	@Autowired
	EnrollRepository enrollRepository;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	PostRepository postRepository;
	@Autowired
	CommonMemberService commonMemberService;
	@Autowired
	EnrollFacade enrollFacade;

	@AfterEach
	void remove() {
		enrollRepository.deleteAll();
		productRepository.deleteAll();
		postRepository.deleteAll();
		memberRepository.deleteAll();
	}

	@Test
	@DisplayName("구매 참여 동시성 테스트")
	void joinEnrollConcurrencyTest() throws InterruptedException, ExecutionException {
		int threadCount = 50;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);

		Member member = memberRepository.save(aMember().build());
		Post post = postRepository.save(aPost().member(member).build());
		Long productId = productRepository.save(aProduct().post(post).build()).getProductId();
		List<Member> members = new ArrayList<>();
		for (int i = 0; i < threadCount; i++) {
			members.add(memberRepository.save(aMember().email("ksw" + i + "@gmail.com").build()));
		}

		for (Member savedMember : members) {
			executorService.execute(() -> {
				try {
					enrollFacade.joinBuying(savedMember.getMemberId(),
						aJoinEnrollDTOFixture().productId(productId).quantity(1L).build());
				} catch (CustomException e) {
					e.printStackTrace();
				} finally {
					countDownLatch.countDown();
				}

			});
		}
		countDownLatch.await();

		Product savedProduct = productRepository.getByProductId(productId);
		assertEquals(threadCount, savedProduct.getSoldQuantity());
	}

	@Test
	@DisplayName("구매 취소 동시성 테스트")
	void cancelEnrollConcurrencyTest() throws InterruptedException, ExecutionException {
		int threadCount = 50;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);

		Member member = memberRepository.save(aMember().build());
		Post savedPost = postRepository.save(aPost().member(member).build());
		Product product = productRepository.save(aProduct().post(savedPost).soldQuantity(50L).build());
		List<Member> members = new ArrayList<>();
		for (int i = 0; i < threadCount; i++) {
			Member savedMember = memberRepository.save(aMember().email("ksw" + i + "@gmail.com").build());
			members.add(savedMember);
		}

		for (Member savedMember : members) {
			executorService.execute(() -> {
				try {
					Enroll savedEnroll = enrollRepository.save(new Enroll(savedMember, product, 1L));
					enrollFacade.cancelBuying(savedMember.getMemberId(), new CancelEnrollDTO(product.getProductId()),
						savedEnroll.getEnrollId());
				} catch (CustomException e) {
					e.printStackTrace();
				} finally {
					countDownLatch.countDown();
				}

			});
		}
		countDownLatch.await();

		Product updatedProduct = productRepository.getByProductId(product.getProductId());
		assertEquals(0L, updatedProduct.getSoldQuantity());
	}

}
