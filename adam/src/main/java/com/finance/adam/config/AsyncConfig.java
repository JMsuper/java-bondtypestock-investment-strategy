package com.finance.adam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

// 작업 처리 시나리오
// 새 작업 요청 →
// 1) 활성 스레드 < 10 → 즉시 실행
// 2) 활성 스레드 = 10, Queue < 500 → Queue에 적재
// 3) Queue = 500, 활성 스레드 < 50 → 새 스레드 생성
// 4) 활성 스레드 = 50, Queue = 500 → 작업 거부

// 비동기 I/O 대기 시나리오
// [Thread-1]
// 작업1 시작 → HTTP 요청 발송 → WAITING (I/O 대기) → 
// 작업2 시작 → HTTP 요청 발송 → WAITING (I/O 대기) →
// 작업3 시작 ...

// [Thread-2]
// 작업4 시작 → HTTP 요청 발송 → WAITING (I/O 대기) →
// 작업5 시작 → HTTP 요청 발송 → WAITING (I/O 대기) →
// 작업6 시작 ...

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 비동기 스레드간의 데드락을 방지하기 위해 우선 높게 설정
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        
        // Queue 크기는 넉넉하게 유지 (대기 작업용)
        executor.setQueueCapacity(500);
        
        // 스레드 이름 접두사 설정
        executor.setThreadNamePrefix("Async-");
        
        // 셧다운 시 대기 작업 완료 허용
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // 셧다운 시 대기 시간 설정
        executor.setAwaitTerminationSeconds(5);
        
        executor.initialize();
        return executor;
    }
}
