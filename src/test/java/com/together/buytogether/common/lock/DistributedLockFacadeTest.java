package com.together.buytogether.common.lock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DistributedLockFacadeTest {

	@Autowired
	DistributedLockFacade distributedLockFacade;

	@Test
	@DisplayName("다른 스레드가 락을 잡고 있어 획득에 실패하면 락 획득 실패 예외가 그대로 전달된다")
	void lockAcquisitionFailureIsNotMaskedByUnlock() throws Exception {
		String lockName = "lock-test-" + UUID.randomUUID();
		CountDownLatch holderAcquired = new CountDownLatch(1);
		CountDownLatch releaseHolder = new CountDownLatch(1);
		ExecutorService executorService = Executors.newSingleThreadExecutor();

		try {
			Future<?> holder = executorService.submit(() ->
				distributedLockFacade.executeWithLock(lockName, 1000, 10_000, () -> {
					holderAcquired.countDown();
					awaitQuietly(releaseHolder);
					return null;
				}));
			assertThat(holderAcquired.await(5, TimeUnit.SECONDS)).isTrue();

			assertThatThrownBy(() -> distributedLockFacade.executeWithLock(lockName, 100, 3000, () -> "never"))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageStartingWith("락 획득 실패");

			releaseHolder.countDown();
			holder.get(5, TimeUnit.SECONDS);
		} finally {
			releaseHolder.countDown();
			executorService.shutdown();
		}
	}

	@Test
	@DisplayName("작업이 끝나기 전에는 다른 스레드가 같은 락으로 진입하지 않는다")
	void lockIsHeldUntilActionCompletes() throws Exception {
		String lockName = "lock-test-" + UUID.randomUUID();
		AtomicInteger inFlight = new AtomicInteger();
		AtomicInteger maxInFlight = new AtomicInteger();
		CountDownLatch start = new CountDownLatch(1);
		ExecutorService executorService = Executors.newFixedThreadPool(2);

		// lease(200ms)보다 긴 작업(600ms)을 두 스레드가 같은 락으로 실행한다
		Callable<Object> task = () -> {
			start.await();
			return distributedLockFacade.executeWithLock(lockName, 2000, 200, () -> {
				maxInFlight.accumulateAndGet(inFlight.incrementAndGet(), Math::max);
				sleepQuietly(600);
				inFlight.decrementAndGet();
				return null;
			});
		};
		try {
			Future<Object> first = executorService.submit(task);
			Future<Object> second = executorService.submit(task);
			start.countDown();
			first.get(5, TimeUnit.SECONDS);
			second.get(5, TimeUnit.SECONDS);
		} finally {
			executorService.shutdown();
		}

		assertThat(maxInFlight.get()).isEqualTo(1);
	}

	private static void sleepQuietly(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private static void awaitQuietly(CountDownLatch latch) {
		try {
			latch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
