package com.together.buytogether.cache.singleflight;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.together.buytogether.cache.dto.SingleFlightCacheData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RedisCacheService {
	private final RedisTemplate<String, Object> redisTemplate;

	public RedisCacheService(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public SingleFlightCacheData get(String key) {
		Object value = redisTemplate.opsForValue().get(key);
		if (value == null) {
			return null;
		}
		return (SingleFlightCacheData)value;
	}

	public void put(String key, SingleFlightCacheData singleFlightCacheData, Long timeToLiveMillis) {
		redisTemplate.opsForValue().set(key, singleFlightCacheData, Duration.ofMillis(timeToLiveMillis));
	}

	public boolean evict(String key) {
		return redisTemplate.delete(key);
	}

	public boolean getLock(String key, Long timeToLiveMillis) {
		Boolean result = redisTemplate.opsForValue().setIfAbsent(key, key, Duration.ofMillis(timeToLiveMillis));
		return Boolean.TRUE.equals(result);
	}

	public void unlock(String key) {
		redisTemplate.delete(key);
	}
}
