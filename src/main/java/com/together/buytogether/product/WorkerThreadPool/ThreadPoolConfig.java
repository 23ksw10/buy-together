package com.together.buytogether.product.WorkerThreadPool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {

	@Bean
	public ThreadPoolTaskExecutor workerThreadPool() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(16);
		executor.setMaxPoolSize(24);
		executor.setQueueCapacity(50);
		executor.setThreadNamePrefix("Worker-");
		executor.initialize();
		return executor;
	}
}
