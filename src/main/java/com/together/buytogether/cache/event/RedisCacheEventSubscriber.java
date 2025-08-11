package com.together.buytogether.cache.event;

import org.springframework.cache.Cache;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.together.buytogether.cache.dto.CacheEvictMessage;
import com.together.buytogether.cache.manager.LocalCacheManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisCacheEventSubscriber implements MessageListener {
	private final RedisTemplate<String, Object> redisTemplate;
	private final LocalCacheManager localCacheManager;
	private final ObjectMapper objectMapper;

	public RedisCacheEventSubscriber(
		RedisTemplate<String, Object> redisTemplate,
		LocalCacheManager localCacheManager,
		ObjectMapper objectMapper) {
		this.redisTemplate = redisTemplate;
		this.localCacheManager = localCacheManager;
		this.objectMapper = objectMapper;
	}

	@Override
	public void onMessage(final Message message, final byte[] pattern) {
		try {
			CacheEvictMessage eventMessage = objectMapper.readValue(message.getBody(), CacheEvictMessage.class);
			log.info("onMessage");
			Cache cache = localCacheManager.getCache(eventMessage.cacheName());
			if (cache == null) {
				log.warn("Cache not found for name: {}", eventMessage.cacheName());
				return;
			}
			cache.evict(eventMessage.key());
			log.info("Evicted  cache '{}' for key: {}", eventMessage.cacheName(), eventMessage.key());

		} catch (Exception e) {
			log.error("Error while processing cache event message.", e);
		}
	}
}
