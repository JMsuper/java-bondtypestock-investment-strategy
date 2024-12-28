package com.finance.adam.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CacheServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CacheService cacheService;

    private final String testUrl1 = "http://test1.com";
    private final String testUrl2 = "http://test2.com";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cacheService, "urls", Arrays.asList(testUrl1, testUrl2));
    }

    @Test
    void whenAllRequestsSucceed_thenNoRetry() {
        // Given
        ResponseEntity<String> successResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)
        )).thenReturn(successResponse);

        // When
        cacheService.refreshCache();

        // Then
        verify(restTemplate, times(2)).exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)
        );
    }

    @Test
    void whenFirstRequestFails_thenRetryOnce() {
        // Given
        ResponseEntity<String> failResponse = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        ResponseEntity<String> successResponse = new ResponseEntity<>(HttpStatus.OK);
        
        when(restTemplate.exchange(
            eq(testUrl1),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)
        ))
            .thenReturn(failResponse)
            .thenReturn(successResponse);
        
        when(restTemplate.exchange(
            eq(testUrl2),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)
        )).thenReturn(successResponse);

        // When
        cacheService.refreshCache();

        // Then
        verify(restTemplate, times(3)).exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)
        );
    }

    @Test
    void whenRequestThrowsException_thenRetryOnce() {
        // Given
        when(restTemplate.exchange(
            eq(testUrl1),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)
        ))
            .thenThrow(new RestClientException("Connection refused"))
            .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        when(restTemplate.exchange(
            eq(testUrl2),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        // When
        cacheService.refreshCache();

        // Then
        verify(restTemplate, times(3)).exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)
        );
    }

    @Test
    void verifyHeadersAreCorrect() {
        // Given
        ResponseEntity<String> successResponse = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)
        )).thenReturn(successResponse);

        // When
        cacheService.refreshCache();

        // Then
        verify(restTemplate, times(2)).exchange(
            anyString(),
            eq(HttpMethod.GET),
            argThat(entity -> 
                "no-cache".equals(entity.getHeaders().getCacheControl())
            ),
            eq(String.class)
        );
    }
} 