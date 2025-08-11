package com.together.buytogether.cache.event;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.together.buytogether.cache.dto.CacheEvictMessage;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RedisCacheEventPublisher {
	private final RedisTemplate<String, Object> redisTemplate;

	public RedisCacheEventPublisher(final RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
		this.redisTemplate = redisTemplate;
	}

	public void publishEvict(String cacheName, Object key) {
		CacheEvictMessage message = new CacheEvictMessage(cacheName, key);
		publish(message);
	}

	public void publish(CacheEvictMessage message) {
		redisTemplate.convertAndSend("CACHE_EVENT_CHANNEL", message);
		log.info("Published cache event message: {}", message);
	}
}
