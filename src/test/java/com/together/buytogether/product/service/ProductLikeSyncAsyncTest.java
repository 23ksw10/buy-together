package com.together.buytogether.product.service;

import static com.together.buytogether.member.domain.MemberFixture.*;
import static com.together.buytogether.product.domain.ProductFixture.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.product.domain.Product;
import com.together.buytogether.product.domain.ProductLikeRepository;
import com.together.buytogether.product.domain.ProductRepository;

@SpringBootTest
public class ProductLikeSyncAsyncTest {

	List<Long> memberIds = new ArrayList<>();
	Long productId;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductLikeRepository productLikeRepository;
	@Autowired
	private AsyncLikeService asyncLikeService;
	@Autowired
	private ProductService productService;

	@BeforeEach
	void setup() {
		Member member = memberRepository.save(aMember().build());
		memberIds.add(member.getMemberId());
		Product product = aProduct().member(member).build();
		productId = productRepository.save(product).getProductId();
		for (int i = 0; i < 99; i++) {
			member = memberRepository.save(aMember().email("kimsw" + i + "@gmail.com").build());
			memberIds.add(member.getMemberId());
		}
	}

	@Test
	@DisplayName("동기")
	void testSyncLikePerformance() throws InterruptedException {
		long start = System.currentTimeMillis();
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);
		for (Long memberId : memberIds) {
			executorService.execute(() -> {
				try {
					productService.likeProduct(memberId, productId);
				} catch (CustomException e) {
					e.printStackTrace();
				} finally {
					countDownLatch.countDown();
				}
			});
		}
		countDownLatch.await();

		long end = System.currentTimeMillis();
		long elapsed = end - start;

	}

	@Test
	@DisplayName("비동기")
	void testAsyncLikePerformance() throws InterruptedException {
		long start = System.currentTimeMillis();
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);
		for (Long memberId : memberIds) {
			executorService.execute(() -> {
				try {
					asyncLikeService.enqueueLike(memberId, productId);
				} catch (CustomException e) {
					e.printStackTrace();
				} finally {
					countDownLatch.countDown();
				}
			});
		}
		countDownLatch.await();
		Thread.sleep(5000);

		long end = System.currentTimeMillis();
		long elapsed = end - start;
	}

	@Test
	@DisplayName("비동기")
	void duplicate() throws InterruptedException {
		long start = System.currentTimeMillis();
		Long productId = 1L;
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(31);
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);
		List<Long> memberIds = new ArrayList<>();
		for (long i = 1; i <= 100; i++) {
			memberIds.add(i);
		}
		for (Long memberId : memberIds) {
			executorService.execute(() -> {
				try {
					asyncLikeService.enqueueLike(memberId, productId);
				} catch (CustomException e) {
					e.printStackTrace();
				} finally {
					countDownLatch.countDown();
				}
			});
		}
		countDownLatch.await();
		Thread.sleep(10000);

		long end = System.currentTimeMillis();
		long elapsed = end - start;
	}

	@Test
	void delete() {
		productLikeRepository.deleteAll();
	}
}
