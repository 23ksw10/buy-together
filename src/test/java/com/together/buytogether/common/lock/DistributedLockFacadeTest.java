package com.together.buytogether.common.lock;

import static com.google.common.util.concurrent.Uninterruptibles.awaitUninterruptibly;
import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;

@SpringBootTest
class DistributedLockFacadeTest {

	@Autowired
	DistributedLockFacade distributedLockFacade;

	@Value("${spring.redis.host}")
	String redisHost;

	@Value("${spring.redis.port}")
	int redisPort;

	@Test
	@DisplayName("다른 스레드가 락을 잡고 있어 획득에 실패하면 일시적 처리 불가(503) 예외가 그대로 전달된다")
	void lockAcquisitionFailureIsNotMaskedByUnlock() throws Exception {
		String lockName = "lock-test-" + UUID.randomUUID();
		CountDownLatch holderAcquired = new CountDownLatch(1);
		CountDownLatch releaseHolder = new CountDownLatch(1);
		ExecutorService executorService = Executors.newSingleThreadExecutor();

		try {
			Future<?> holder = executorService.submit(() ->
				distributedLockFacade.executeWithLock(lockName, 1000, () -> {
					holderAcquired.countDown();
					awaitUninterruptibly(releaseHolder);
					return null;
				}));
			assertThat(holderAcquired.await(5, TimeUnit.SECONDS)).isTrue();

			// 같은 요청을 다시 보내면 성공할 수 있는 서버 쪽 사정이므로 503으로 응답해야 한다
			assertThatThrownBy(() -> distributedLockFacade.executeWithLock(lockName, 100, () -> "never"))
				.isInstanceOfSatisfying(CustomException.class, e ->
					assertThat(e.getErrorCode()).isEqualTo(ErrorCode.LOCK_ACQUISITION_FAILED));

			releaseHolder.countDown();
			holder.get(5, TimeUnit.SECONDS);
		} finally {
			releaseHolder.countDown();
			executorService.shutdown();
		}
	}

	@Test
	@DisplayName("작업이 락의 최초 만료 시간보다 길어져도 끝나기 전에는 다른 스레드가 같은 락으로 진입하지 않는다")
	void lockIsHeldUntilActionCompletes() throws Exception {
		// watchdog timeout을 1초로 줄인 클라이언트로, 3초 작업 동안 락이 연장되어야만 통과하게 한다
		Config config = new Config();
		config.setLockWatchdogTimeout(1000);
		config.useSingleServer().setAddress("redis://" + redisHost + ":" + redisPort);
		RedissonClient redissonClient = Redisson.create(config);
		DistributedLockFacade facade = new DistributedLockFacade(redissonClient);

		String lockName = "lock-test-" + UUID.randomUUID();
		AtomicInteger inFlight = new AtomicInteger();
		AtomicInteger maxInFlight = new AtomicInteger();
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		Callable<Object> task = () -> facade.executeWithLock(lockName, 5000, () -> {
			maxInFlight.accumulateAndGet(inFlight.incrementAndGet(), Math::max);
			sleepUninterruptibly(3, TimeUnit.SECONDS);
			inFlight.decrementAndGet();
			return null;
		});

		try {
			for (Future<Object> future : executorService.invokeAll(List.of(task, task), 10, TimeUnit.SECONDS)) {
				future.get();
			}
		} finally {
			executorService.shutdown();
			redissonClient.shutdown();
		}

		assertThat(maxInFlight.get()).isEqualTo(1);
	}
}
