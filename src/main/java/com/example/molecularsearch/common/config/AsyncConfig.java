package com.example.molecularsearch.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    /* Async Thread 설정 */
    @Override
    public Executor getAsyncExecutor() {
        int processors = Runtime.getRuntime().availableProcessors();    // PC의 Processor 개수

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor(); // Thread Pool 관리
        executor.setCorePoolSize(processors);   // 기본 Thread 개수
        executor.setMaxPoolSize(processors * 2);    // 최대 Thread 개수
        executor.setQueueCapacity(50);  // 대기를 위한 Queue Size
        executor.setKeepAliveSeconds(60);   // Thread 재사용 시간
        executor.setThreadNamePrefix("AsyncExecutor-"); // Thread 이름의 Prefix
        executor.initialize();  // ThreadPoolExecutor 생성

        return executor;
    }
}
