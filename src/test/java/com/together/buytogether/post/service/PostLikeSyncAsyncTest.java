package com.together.buytogether.post.service;

import static com.together.buytogether.member.domain.MemberFixture.*;
import static com.together.buytogether.post.domain.PostFixture.*;

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
import com.together.buytogether.post.domain.Post;
import com.together.buytogether.post.domain.PostLikeRepository;
import com.together.buytogether.post.domain.PostRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class PostLikeSyncAsyncTest {

	List<Long> memberIds = new ArrayList<>();
	Long postId;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private PostLikeRepository postLikeRepository;
	@Autowired
	private AsyncLikeService asyncLikeService;
	@Autowired
	private PostService postService;

	@BeforeEach
	void setup() {
		Member member = memberRepository.save(aMember().build());
		memberIds.add(member.getMemberId());
		Post post = aPost().member(member).build();
		postId = postRepository.save(post).getPostId();
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
					postService.likePost(memberId, postId);
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
					asyncLikeService.enqueueLike(memberId, postId);
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
		Long postId = 1L;
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
					asyncLikeService.enqueueLike(memberId, postId);
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
		postLikeRepository.deleteAll();
	}
}
