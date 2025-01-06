package com.finance.adam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apache.http.util.EntityUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {
    private static final int SUCCESS_STATUS = 200;

    @Value("${cache.server.url-1}")
    private String CACHE_SERVER_URL_1;
    @Value("${cache.server.url-2}")
    private String CACHE_SERVER_URL_2;
    @Value("${cache.client.origin}")
    private String CACHE_CLIENT_ORIGIN;

    private final CloseableHttpClient httpClient;

    public boolean sendRequest(String url) {
        HttpUriRequest request = RequestBuilder.get()
                .setUri(url)
                .setHeader(HttpHeaders.CACHE_CONTROL, "no-cache")
                .setHeader("Origin", CACHE_CLIENT_ORIGIN)
                .build();
                
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            try {
                int statusCode = response.getStatusLine().getStatusCode();
                log.info("Cache server response status code: {}", statusCode);
                return statusCode == SUCCESS_STATUS;
            } finally {
                EntityUtils.consume(response.getEntity());
            }
        } catch (IOException e) {
            log.error("Error sending request to cache server: {}", e.getMessage());
            return false;
        }
    }

    @Async
    public CompletableFuture<Void> refreshCache() {
        List<String> urls = List.of(CACHE_SERVER_URL_1,CACHE_SERVER_URL_2);

        urls.forEach(url -> {
            if (!sendRequest(url)) {
                log.info("Retrying cache refresh for URL: {}", url);
                if(!sendRequest(url)) {
                    log.error("Cache refresh failed for URL: {}", url);
                }
            }
        });

        return CompletableFuture.completedFuture(null);
    }
}
