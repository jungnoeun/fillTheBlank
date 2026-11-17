package com.code.fill_the_blank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class WebSocketSchedulerConfig { // websocket 만료 처리 위한 Scheduler 추가

	@Bean
	public ThreadPoolTaskScheduler websocketTaskScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(1);
		scheduler.setThreadNamePrefix("ws-expiry-");
		
		return scheduler;
	}
}
