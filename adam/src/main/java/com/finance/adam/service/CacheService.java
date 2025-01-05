package com.finance.adam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {
    
    private final RestTemplate restTemplate;
    
    @Value("${cache.server.url-1}")
    private String CACHE_SERVER_URL_1;
    @Value("${cache.server.url-2}")
    private String CACHE_SERVER_URL_2;

    private boolean sendRequest(String url) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setCacheControl("no-cache");
            headers.setOrigin("https://snowball-stock.vercel.app");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Cache refresh successful for URL: {}", url);
                return true;
            } else {
                log.warn("Cache refresh failed for URL: {}. Status: {}", 
                        url, response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            log.warn("Error refreshing cache for URL: {}. Error: {}", 
                    url, e.getMessage());
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
