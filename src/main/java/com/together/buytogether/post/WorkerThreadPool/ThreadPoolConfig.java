package com.together.buytogether.post.WorkerThreadPool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {

	@Bean
	public ThreadPoolTaskExecutor workerThreadPool() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(50);
		executor.setThreadNamePrefix("Worker-");
		executor.initialize();
		return executor;
	}
}
