package com.together.buytogether.cache.dto;

public record SingleFlightCacheData<T>(
	T data,
	Long decisionForUpdate,
	Long maxAttemptRefreshCache,
	Long createdAt,
	Long timeToMillis
) {

}
