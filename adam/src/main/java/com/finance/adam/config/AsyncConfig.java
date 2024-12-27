package com.finance.adam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 스레드 풀이 실행을 시작할 때 생성되는 스레드의 수를 정의하며, 스레드 풀의 기본 사이즈
        // 스케줄링의 태스크 수와 일치시킴(4개)
        executor.setCorePoolSize(10);
        // 스레드 풀이 확장될 수 있는 스레드의 상한선을 설정하며, 스레드 풀이 관리할 수 있는 최대 스레드 수를 정의
        executor.setMaxPoolSize(50);
        // corePoolSize가 가득 찬 상태에서 추가 작업을 처리할 수 없을 때 대기하는 작업의 최대 개수를 정의
        executor.setQueueCapacity(500);
        // 생성되는 스레드의 이름 접두사를 정의
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }
}
