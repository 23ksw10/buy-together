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
		if (!tryLock(lock, waitMilliSecond, leaseMilliSecond)) {
			throw new IllegalStateException("락 획득 실패" + lockName);
		}
		try {
			return action.get();
		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}

	private boolean tryLock(RLock lock, long waitMilliSecond, long leaseMilliSecond) {
		try {
			return lock.tryLock(waitMilliSecond, leaseMilliSecond, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Lock interrupted", e);
		}
	}
}
