package com.finance.adam.config;

import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public LogbackMetrics logbackMetrics() {
        return new LogbackMetrics();
    }
}
