package com.finance.adam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {
    private static final String ORIGIN_URL = "https://snowball-stock.vercel.app";
    private static final int SUCCESS_STATUS = 200;
    
    private final HttpClient httpClient = HttpClients.custom().build();

    @Value("${cache.server.url-1}")
    private String CACHE_SERVER_URL_1;
    @Value("${cache.server.url-2}")
    private String CACHE_SERVER_URL_2;

    private boolean sendRequest(String url) {
        HttpUriRequest request = RequestBuilder.get()
                .setUri(url)
                .setHeader(HttpHeaders.CACHE_CONTROL, "no-cache")
                .setHeader("Origin", ORIGIN_URL)
                .build();
        try {
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            log.info("Cache server response status code: {}", statusCode);
            return statusCode == SUCCESS_STATUS;
        } catch (IOException e) {
            log.error("Error sending request to cache server: {}", e.getMessage());
            return false;
        }
    }

    @Async
    public void refreshCache() {
        List<String> urls = List.of(CACHE_SERVER_URL_1,CACHE_SERVER_URL_2);

        urls.forEach(url -> {
            if (!sendRequest(url)) {
                log.info("Retrying cache refresh for URL: {}", url);
                if(!sendRequest(url)) {
                    log.error("Cache refresh failed for URL: {}", url);
                }
            }
        });
    }
}
