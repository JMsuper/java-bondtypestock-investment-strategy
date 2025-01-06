package com.finance.adam.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfig {

    @Bean
    public CloseableHttpClient httpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)         // 연결 타임아웃: 3초
                .setSocketTimeout(5000)         // 소켓 타임아웃: 5초
                .setConnectionRequestTimeout(5000) // 연결 요청 타임아웃: 5초
                .build();

        return HttpClients.custom()
                .setMaxConnTotal(100)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }
}
