package com.together.buytogether.common.lock;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import com.together.buytogether.common.error.CustomException;
import com.together.buytogether.common.error.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DistributedLockFacade {
	private final RedissonClient redissonClient;

	public <T> T executeWithLock(String lockName, long waitMilliSecond, Supplier<T> action) {
		RLock lock = redissonClient.getLock(lockName);
		if (!tryLock(lock, waitMilliSecond)) {
			throw new CustomException(ErrorCode.LOCK_ACQUISITION_FAILED);
		}
		try {
			return action.get();
		} finally {
			lock.unlock();
		}
	}

	private boolean tryLock(RLock lock, long waitMilliSecond) {
		try {
			return lock.tryLock(waitMilliSecond, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Lock interrupted", e);
		}
	}
}
