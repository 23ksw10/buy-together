package com.together.buytogether.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SingleFlightCacheable {
	String cacheName();

	String key();

	long localTimeToLiveMillis();

	long redisTimeToLiveMillis();

	long decisionForUpdate() default 90;

	long maxAttemptRefreshCache() default 1000;
}

