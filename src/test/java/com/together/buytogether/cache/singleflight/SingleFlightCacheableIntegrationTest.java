package com.together.buytogether.cache.singleflight;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.together.buytogether.annotation.SingleFlightCacheable;

/**
 * End-to-end test of {@link SingleFlightCacheable} through the real aspect, Caffeine local cache and
 * a Testcontainers-backed Redis. Uses a test-only counting bean (short-circuits the DB) so we can
 * assert exactly how many times the business logic ran.
 *
 * <p>Both the cold-start ({@code NEVER_CACHED}) path and the {@code LOCAL_EXPIRED_REDIS_EXPIRED}
 * path are lock-protected, so concurrent callers run the business logic exactly once in either
 * case. {@link #singleFlightOnColdStart()} covers the first-ever burst (using a dedicated
 * cacheName so the state is deterministically {@code NEVER_CACHED}); {@link #singleFlightUnderConcurrency()}
 * primes then evicts both tiers to exercise the expired-and-contested state.
 */
@SpringBootTest
@ActiveProfiles("singleflight-it")
@Testcontainers
@Import(SingleFlightCacheableIntegrationTest.TestBeans.class)
class SingleFlightCacheableIntegrationTest {

	@Container
	static final GenericContainer<?> REDIS =
		new GenericContainer<>(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379);

	@DynamicPropertySource
	static void redisProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.redis.host", REDIS::getHost);
		registry.add("spring.redis.port", () -> REDIS.getMappedPort(6379));
	}

	@Autowired
	private CountingService countingService;

	@Autowired
	private ColdStartCountingService coldStartCountingService;

	@Autowired
	private SingleFlightCacheManager singleFlightCacheManager;

	@BeforeEach
	void resetCounter() {
		countingService.reset();
		coldStartCountingService.reset();
	}

	@Test
	@DisplayName("최초 호출 이후 동일 키 재호출은 캐시 히트로 비즈니스 로직을 재실행하지 않는다")
	void cacheHitDoesNotReExecute() {
		Long id = 1L;

		String first = countingService.getValue(id);
		String second = countingService.getValue(id);

		assertThat(first).isEqualTo("value-" + id);
		assertThat(second).isEqualTo("value-" + id);
		assertThat(countingService.executionCount()).isEqualTo(1);
	}

	@Test
	@DisplayName("로컬·Redis 캐시가 모두 만료된 상태에서 동시 50요청이 와도 비즈니스 로직은 단 한 번만 실행된다")
	void singleFlightUnderConcurrency() throws InterruptedException {
		Long id = 2L;
		// Prime the cache so we leave NEVER_CACHED (which has no lock), then force the contested
		// LOCAL_EXPIRED_REDIS_EXPIRED state deterministically by evicting both tiers.
		countingService.getValue(id);
		String cacheKey = CountingService.CACHE_NAME + "::" + id;
		singleFlightCacheManager.evictFromLocal(CountingService.CACHE_NAME, cacheKey);
		singleFlightCacheManager.evictFromRedis(cacheKey);
		countingService.reset();

		int threadCount = 50;
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		CountDownLatch ready = new CountDownLatch(threadCount);
		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch done = new CountDownLatch(threadCount);
		List<String> results = Collections.synchronizedList(new ArrayList<>());
		List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());

		for (int i = 0; i < threadCount; i++) {
			executor.execute(() -> {
				ready.countDown();
				try {
					start.await();
					results.add(countingService.getValue(id));
				} catch (Throwable t) {
					errors.add(t);
				} finally {
					done.countDown();
				}
			});
		}

		ready.await();
		start.countDown();
		boolean finished = done.await(30, TimeUnit.SECONDS);
		executor.shutdownNow();

		assertThat(finished).as("all threads completed").isTrue();
		assertThat(errors).as("no thread threw").isEmpty();
		assertThat(countingService.executionCount())
			.as("business logic executed exactly once under single-flight")
			.isEqualTo(1);
		assertThat(results).hasSize(threadCount).allMatch(v -> v.equals("value-" + id));
	}

	@Test
	@DisplayName("캐시가 한 번도 채워지지 않은 콜드 스타트에서 동시 50요청이 와도 비즈니스 로직은 단 한 번만 실행된다")
	void singleFlightOnColdStart() throws InterruptedException {
		// Dedicated cacheName whose local cache instance never existed, so the state is
		// deterministically NEVER_CACHED on the first burst — no priming/eviction needed.
		Long id = 1L;

		int threadCount = 50;
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		CountDownLatch ready = new CountDownLatch(threadCount);
		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch done = new CountDownLatch(threadCount);
		List<String> results = Collections.synchronizedList(new ArrayList<>());
		List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());

		for (int i = 0; i < threadCount; i++) {
			executor.execute(() -> {
				ready.countDown();
				try {
					start.await();
					results.add(coldStartCountingService.getValue(id));
				} catch (Throwable t) {
					errors.add(t);
				} finally {
					done.countDown();
				}
			});
		}

		ready.await();
		start.countDown();
		boolean finished = done.await(30, TimeUnit.SECONDS);
		executor.shutdownNow();

		assertThat(finished).as("all threads completed").isTrue();
		assertThat(errors).as("no thread threw").isEmpty();
		assertThat(coldStartCountingService.executionCount())
			.as("business logic executed exactly once on cold start")
			.isEqualTo(1);
		assertThat(results).hasSize(threadCount).allMatch(v -> v.equals("value-" + id));
	}

	@TestConfiguration
	static class TestBeans {
		@Bean
		CountingService countingService() {
			return new CountingService();
		}

		@Bean
		ColdStartCountingService coldStartCountingService() {
			return new ColdStartCountingService();
		}
	}

	/**
	 * Test-only bean: a {@link SingleFlightCacheable} method that counts executions instead of
	 * hitting the DB. Returns a plain String so it round-trips cleanly through the Redis JSON
	 * serializer. TTLs are generous so nothing expires mid-test — the concurrency test controls
	 * cache state explicitly via eviction.
	 */
	static class CountingService {
		static final String CACHE_NAME = "itTest";

		private final AtomicInteger executionCount = new AtomicInteger();

		@SingleFlightCacheable(
			cacheName = CACHE_NAME,
			key = "#id",
			redisTimeToLiveMillis = 5000,
			localTimeToLiveMillis = 5000)
		public String getValue(Long id) {
			executionCount.incrementAndGet();
			return "value-" + id;
		}

		int executionCount() {
			return executionCount.get();
		}

		void reset() {
			executionCount.set(0);
		}
	}

	/**
	 * Same as {@link CountingService} but with its own {@code cacheName}, so its local cache instance
	 * never exists until first call — giving the cold-start ({@code NEVER_CACHED}) concurrency test a
	 * deterministic starting state regardless of test ordering.
	 */
	static class ColdStartCountingService {
		static final String CACHE_NAME = "itColdStart";

		private final AtomicInteger executionCount = new AtomicInteger();

		@SingleFlightCacheable(
			cacheName = CACHE_NAME,
			key = "#id",
			redisTimeToLiveMillis = 5000,
			localTimeToLiveMillis = 5000)
		public String getValue(Long id) {
			executionCount.incrementAndGet();
			return "value-" + id;
		}

		int executionCount() {
			return executionCount.get();
		}

		void reset() {
			executionCount.set(0);
		}
	}
}
