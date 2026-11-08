package com.together.buytogether.enroll.service;

import static com.together.buytogether.enroll.domain.JoinEnrollDTOFixture.*;
import static com.together.buytogether.member.domain.MemberFixture.*;
import static com.together.buytogether.product.domain.ProductFixture.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.service.CommonMemberService;
import com.together.buytogether.enroll.domain.Enroll;
import com.together.buytogether.enroll.domain.EnrollRepository;
import com.together.buytogether.enroll.dto.request.CancelEnrollDTO;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.product.domain.Product;
import com.together.buytogether.product.domain.ProductRepository;

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
	CommonMemberService commonMemberService;
	@Autowired
	EnrollFacade enrollFacade;
	@Autowired
	JdbcTemplate jdbcTemplate;

	private final Queue<Long> createdMemberIds = new ConcurrentLinkedQueue<>();
	private final Queue<Long> createdProductIds = new ConcurrentLinkedQueue<>();
	private final Queue<Long> createdEnrollIds = new ConcurrentLinkedQueue<>();
	private String emailPrefix;

	@BeforeEach
	void setUp() {
		emailPrefix = "enroll-concurrency-" + UUID.randomUUID();
	}

	@AfterEach
	void cleanupTestData() {
		enrollRepository.deleteAllByIdInBatch(createdEnrollIds);
		productRepository.deleteAllByIdInBatch(createdProductIds);
		memberRepository.deleteAllByIdInBatch(createdMemberIds);

		createdEnrollIds.clear();
		createdProductIds.clear();
		createdMemberIds.clear();
	}

	@Test
	@DisplayName("구매 참여 동시성 테스트")
	void joinEnrollConcurrencyTest() throws InterruptedException, ExecutionException {
		int threadCount = 50;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);

		Member member = saveMember("join-seller", 0);
		Product product = saveProduct(aProduct().member(member).build());
		Long productId = product.getProductId();
		List<Member> members = saveMembers("join-buyer", threadCount);

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
		executorService.shutdown();
		trackEnrollsByMembersAndProduct(members, productId);

		Product savedProduct = productRepository.getByProductId(productId);
		assertEquals(threadCount, savedProduct.getSoldQuantity());
	}

	@Test
	@DisplayName("구매 취소 동시성 테스트")
	void cancelEnrollConcurrencyTest() throws InterruptedException, ExecutionException {
		int threadCount = 50;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);

		Member member = saveMember("cancel-seller", 0);
		Product product = saveProduct(aProduct().member(member).soldQuantity(50L).build());
		List<Member> members = saveMembers("cancel-buyer", threadCount);

		for (Member savedMember : members) {
			executorService.execute(() -> {
				try {
					Enroll savedEnroll = enrollRepository.save(new Enroll(savedMember, product, 1L));
					createdEnrollIds.add(savedEnroll.getEnrollId());
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
		executorService.shutdown();

		Product updatedProduct = productRepository.getByProductId(product.getProductId());
		assertEquals(0L, updatedProduct.getSoldQuantity());
	}

	@Test
	@DisplayName("참여와 취소가 동시에 일어나도 판매 수량은 참여 수량의 합과 같다")
	void joinAndCancelConcurrencyKeepsSoldQuantityConsistent() throws InterruptedException {
		int pairCount = 30;
		ExecutorService executorService = Executors.newFixedThreadPool(pairCount * 2);
		CountDownLatch startLatch = new CountDownLatch(1);
		CountDownLatch doneLatch = new CountDownLatch(pairCount * 2);
		AtomicInteger joinSuccessCount = new AtomicInteger();
		AtomicInteger cancelSuccessCount = new AtomicInteger();

		Member seller = saveMember("mixed-seller", 0);
		Product product = saveProduct(aProduct().member(seller)
			.soldQuantity((long)pairCount)
			.maxQuantity(1000L)
			.build());
		Long productId = product.getProductId();
		List<Member> cancelers = saveMembers("mixed-canceler", pairCount);
		List<Member> joiners = saveMembers("mixed-joiner", pairCount);

		for (int i = 0; i < pairCount; i++) {
			Member joiner = joiners.get(i);
			Member canceler = cancelers.get(i);
			Long enrollId = enrollRepository.save(new Enroll(canceler, product, 1L)).getEnrollId();
			createdEnrollIds.add(enrollId);
			submitAfterStart(executorService, startLatch, doneLatch, joinSuccessCount, () ->
				enrollFacade.joinBuying(joiner.getMemberId(),
					aJoinEnrollDTOFixture().productId(productId).quantity(1L).build()));
			// 실제 DELETE 요청에서는 productId가 바인딩되지 않아 null로 들어온다
			submitAfterStart(executorService, startLatch, doneLatch, cancelSuccessCount, () ->
				enrollFacade.cancelBuying(canceler.getMemberId(), new CancelEnrollDTO(null), enrollId));
		}
		startLatch.countDown();
		doneLatch.await();
		executorService.shutdown();
		trackEnrollsByMembersAndProduct(joiners, productId);

		Long enrolledQuantity = jdbcTemplate.queryForObject(
			"SELECT COALESCE(SUM(quantity), 0) FROM enroll WHERE product_id = ?", Long.class, productId);
		Product savedProduct = productRepository.getByProductId(productId);
		// 요청이 하나도 실행되지 않아도 불변식은 성립하므로, 참여와 취소가 실제로 경합했는지도 확인한다
		assertTrue(joinSuccessCount.get() > 0);
		assertTrue(cancelSuccessCount.get() > 0);
		assertEquals(enrolledQuantity, savedProduct.getSoldQuantity());
	}

	// 개별 요청의 성공/실패와 무관하게 불변식만 검증하므로 예외는 무시하고 성공 횟수만 센다
	private void submitAfterStart(ExecutorService executorService, CountDownLatch startLatch,
		CountDownLatch doneLatch, AtomicInteger successCount, Runnable task) {
		executorService.execute(() -> {
			try {
				startLatch.await();
				task.run();
				successCount.incrementAndGet();
			} catch (Exception ignored) {
			} finally {
				doneLatch.countDown();
			}
		});
	}

	private List<Member> saveMembers(String role, int count) {
		List<Member> members = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			members.add(saveMember(role, i));
		}
		return members;
	}

	private Member saveMember(String role, int index) {
		Member member = memberRepository.save(aMember()
			.email(uniqueEmail(role, index))
			.build());
		createdMemberIds.add(member.getMemberId());
		return member;
	}

	private Product saveProduct(Product product) {
		Product savedProduct = productRepository.save(product);
		createdProductIds.add(savedProduct.getProductId());
		return savedProduct;
	}

	private String uniqueEmail(String role, int index) {
		return emailPrefix + "-" + role + "-" + index + "@test.com";
	}

	private void trackEnrollsByMembersAndProduct(List<Member> members, Long productId) {
		for (Member member : members) {
			enrollRepository.findByMemberIdAndProductId(member.getMemberId(), productId)
				.ifPresent(enroll -> createdEnrollIds.add(enroll.getEnrollId()));
		}
	}

}
