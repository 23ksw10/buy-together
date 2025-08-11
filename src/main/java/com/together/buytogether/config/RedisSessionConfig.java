package com.together.buytogether.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.together.buytogether.cache.event.RedisCacheEventSubscriber;

@Configuration
public class RedisSessionConfig {
	private static final String REDISSON_HOST_PREFIX = "redis://";
	@Value("${spring.redis.port}")
	public int port;
	@Value("${spring.redis.host}")
	public String host;
	@Value("${spring.redis.password:#{null}}")
	private String password;

	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.useSingleServer()
			.setAddress(REDISSON_HOST_PREFIX + host + ":" + port)
			.setPassword(password)
			.setSslEnableEndpointIdentification(false);
		return Redisson.create(config);
	}

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
		redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
		return new LettuceConnectionFactory(redisStandaloneConfiguration);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory());

		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
		return template;
	}

	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(
		RedisConnectionFactory connectionFactory,
		MessageListenerAdapter messageListenerAdapter) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(messageListenerAdapter, new ChannelTopic("CACHE_EVENT_CHANNEL"));
		return container;
	}

	@Bean
	public MessageListenerAdapter messageListenerAdapter(RedisCacheEventSubscriber redisCacheEventSubscriber) {
		return new MessageListenerAdapter(redisCacheEventSubscriber, "onMessage");
	}
}
