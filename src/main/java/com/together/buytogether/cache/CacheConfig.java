package com.together.buytogether.cache;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.together.buytogether.cache.manager.CompositeCacheManager;
import com.together.buytogether.cache.manager.LocalCacheManager;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

	private final RedisConnectionFactory redisConnectionFactory;

	public CacheConfig(RedisConnectionFactory redisConnectionFactory) {
		this.redisConnectionFactory = redisConnectionFactory;
	}

	@Bean(name = "localCacheManager")
	public LocalCacheManager localCacheManager() {
		log.info("로컬 초기화 시작");
		List<Cache> caches = Arrays.stream(CacheName.values())
			.filter(group ->
				group.getCacheType() == CacheType.LOCAL ||
					group.getCacheType() == CacheType.COMPOSITE
			)
			.map(this::toCaffeineCache)
			.collect(Collectors.toList());
		log.info("로컬 초기화 끝");

		return new LocalCacheManager(caches);
	}

	private CaffeineCache toCaffeineCache(CacheName cacheName) {
		return new CaffeineCache(
			cacheName.getCacheName(),
			Caffeine.newBuilder()
				.expireAfterWrite(cacheName.getExpiredAfterWrite().getSeconds(), TimeUnit.SECONDS)
				.recordStats()
				.build()
		);
	}

	@Bean(name = "redisCacheManager")
	public RedisCacheManager redisCacheManager() {
		RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(
				RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
			.entryTtl(Duration.ofMinutes(30));
		Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = Arrays.stream(CacheName.values())
			.filter(it -> it.getCacheType() == CacheType.GLOBAL || it.getCacheType() == CacheType.COMPOSITE)
			.collect(Collectors.toMap(
				CacheName::getCacheName,
				cacheName -> redisCacheConfiguration.entryTtl(cacheName.getExpiredAfterWrite())
			));
		return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory)
			.cacheDefaults(redisCacheConfiguration)
			.withInitialCacheConfigurations(redisCacheConfigurationMap)
			.build();
	}

	@Primary
	@Bean
	public CacheManager compositeCacheManager(CacheManager redisCacheManager,
		LocalCacheManager localCacheManager) {
		return new CompositeCacheManager(Arrays.asList(localCacheManager, redisCacheManager), localCacheManager);
	}
}
