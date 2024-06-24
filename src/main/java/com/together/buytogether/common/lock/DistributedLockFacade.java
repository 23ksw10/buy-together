package com.together.buytogether.common.lock;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DistributedLockFacade {
	private final RedissonClient redissonClient;

	public <T> T executeWithLock(String lockName, long waitMilliSecond, long leaseMilliSecond, Supplier<T> action) {
		RLock lock = redissonClient.getLock(lockName);
		try {
			if (!lock.tryLock(waitMilliSecond, leaseMilliSecond, TimeUnit.MILLISECONDS)) {
				throw new IllegalStateException("락 획득 실패" + lockName);
			}
			return action.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Lock interrupted", e);
		} finally {
			if (lock.isLocked() || lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}
}
