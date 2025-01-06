package com.finance.adam.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ActiveProfiles;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CacheServiceThreadTest {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Test
    void threadPool_관리_반복_테스트() throws InterruptedException {
        int totalIterations = 5;
        long intervalMillis = 1000; // 5초

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        printThreadInfo("테스트 시작", threadMXBean);

        for (int iteration = 1; iteration <= totalIterations; iteration++) {
            System.out.println("\n=== Iteration " + iteration + " of " + totalIterations + " ===");
            
            int threadCount = 10;
            CountDownLatch completionLatch = new CountDownLatch(threadCount);
            
            for (int i = 0; i < threadCount; i++) {
                cacheService.refreshCache().thenRun(completionLatch::countDown);
            }
            
            Assertions.assertTrue(completionLatch.await(10, TimeUnit.SECONDS));
            printThreadInfo("반복 " + iteration + " 완료", threadMXBean);

            if (iteration < totalIterations) {
                Thread.sleep(intervalMillis);
            }
        }

        // 마지막 반복 후 추가 대기 (리소스 정리 시간)
        Thread.sleep(1000);
        
        // 최종 스레드 상태 확인
        printThreadInfo("테스트 종료", threadMXBean);

        // 비동기 스레드 중 WAITING이 아닌 스레드 수 확인
        long asyncThreadCount = Thread.getAllStackTraces().keySet().stream()
                .filter(thread -> thread.getName().startsWith("Async-"))
                .filter(thread -> thread.getState() != Thread.State.WAITING)
                .count();

        System.out.println("\n=== 최종 리소스 사용 현황 ===");
        System.out.println("비동기 스레드 수: " + asyncThreadCount);
        System.out.println("대기 큐 크기: " + executor.getThreadPoolExecutor().getQueue().size());

        // 검증
        assertThat(asyncThreadCount).isEqualTo(0);
        assertThat(executor.getThreadPoolExecutor().getQueue().size()).isEqualTo(0);
    }

    private void printThreadInfo(String phase, ThreadMXBean threadMXBean) {
        System.out.println("\n=== " + phase + " ===");
        System.out.println("Total thread count: " + threadMXBean.getThreadCount());
        
        Thread[] threads = new Thread[threadMXBean.getThreadCount()];
        Thread.enumerate(threads);
        
        Map<String, List<Thread.State>> threadStates = Arrays.stream(threads)
            .filter(Objects::nonNull)
            .filter(thread -> thread.getName().startsWith("Async-"))
            .collect(Collectors.groupingBy(
                thread -> thread.getName().substring(0, 6),
                Collectors.mapping(Thread::getState, Collectors.toList())
            ));

        System.out.println("Async thread states:");
        threadStates.forEach((prefix, states) -> {
            Map<Thread.State, Long> stateCounts = states.stream()
                .collect(Collectors.groupingBy(state -> state, Collectors.counting()));
            System.out.println(prefix + ": " + stateCounts);
        });
    }
}