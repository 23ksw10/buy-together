package com.together.buytogether.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.together.buytogether.annotation.SingleFlightCacheEvict;
import com.together.buytogether.annotation.SingleFlightCacheable;
import com.together.buytogether.cache.CacheKeyGenerator;
import com.together.buytogether.cache.event.RedisCacheEventPublisher;
import com.together.buytogether.cache.singleflight.CacheStrategyExecutor;
import com.together.buytogether.cache.singleflight.SingleFlightCacheManager;

@Aspect
@Component
public class SingleFlightAspect {

	private final CacheKeyGenerator cacheKeyGenerator;
	private final CacheStrategyExecutor cacheStrategyExecutor;
	private final SingleFlightCacheManager singleFlightCacheManager;
	private final RedisCacheEventPublisher redisCacheEventPublisher;

	public SingleFlightAspect(CacheKeyGenerator cacheKeyGenerator,
		CacheStrategyExecutor cacheStrategyExecutor,
		SingleFlightCacheManager singleFlightCacheManager,
		RedisCacheEventPublisher redisCacheEventPublisher) {
		this.cacheKeyGenerator = cacheKeyGenerator;
		this.cacheStrategyExecutor = cacheStrategyExecutor;
		this.singleFlightCacheManager = singleFlightCacheManager;
		this.redisCacheEventPublisher = redisCacheEventPublisher;
	}

	@Around("@annotation(com.together.buytogether.annotation.SingleFlightCacheable)")
	public Object aroundSingleFlightCacheable(ProceedingJoinPoint joinPoint) throws Throwable {
		SingleFlightCacheable annotation = getCacheableAnnotation(joinPoint);
		String cacheKey = cacheKeyGenerator.generate(joinPoint, annotation.key(), annotation.cacheName());

		return cacheStrategyExecutor.executeCacheStrategy(annotation, cacheKey, joinPoint);
	}

	@Around("@annotation(com.together.buytogether.annotation.SingleFlightCacheEvict)")
	public Object aroundReqShieldCacheEvict(ProceedingJoinPoint joinPoint) throws Throwable {
		SingleFlightCacheEvict annotation = getCacheEvictAnnotation(joinPoint);
		String cacheKey = cacheKeyGenerator.generate(joinPoint, annotation.key(), annotation.cacheName());

		singleFlightCacheManager.evictFromLocal(annotation.cacheName(), cacheKey);
		singleFlightCacheManager.evictFromRedis(cacheKey);

		redisCacheEventPublisher.publishEvict(annotation.cacheName(), cacheKey);
		return joinPoint.proceed();
	}

	private SingleFlightCacheable getCacheableAnnotation(ProceedingJoinPoint joinPoint) {
		MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
		Method method = methodSignature.getMethod();
		return method.getAnnotation(SingleFlightCacheable.class);
	}

	private SingleFlightCacheEvict getCacheEvictAnnotation(ProceedingJoinPoint joinPoint) {
		MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
		Method method = methodSignature.getMethod();
		return method.getAnnotation(SingleFlightCacheEvict.class);
	}
}
