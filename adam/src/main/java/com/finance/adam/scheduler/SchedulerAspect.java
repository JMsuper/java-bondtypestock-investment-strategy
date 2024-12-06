package com.finance.adam.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class SchedulerAspect {

    @Value("${scheduler.enabled:false}")
    private boolean schedulerEnabled;

    @Around("@annotation(com.finance.adam.scheduler.ConditionalScheduler)")
    public Object controlSchedulerExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!schedulerEnabled) {
            log.info("Scheduler is disabled. Skipping execution of {}", joinPoint.getSignature().getName());
            return null; // 스케줄러 실행 중단
        }
        log.info("Scheduler is enabled. Executing {}", joinPoint.getSignature().getName());
        return joinPoint.proceed(); // 스케줄러 메서드 실행
    }
}
