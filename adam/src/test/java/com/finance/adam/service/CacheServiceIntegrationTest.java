package com.finance.adam.service;

import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "cache.client.origin=http://test.com")
class CacheServiceIntegrationTest {

    @Autowired
    private CacheService cacheService;

    @MockBean
    private CloseableHttpClient httpClient;

    @Value("${cache.client.origin}")
    private String CACHE_CLIENT_ORIGIN;

    @Test
    void sendRequest_테스트() throws IOException {
        // given
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        when(mockResponse.getStatusLine()).thenReturn(
            new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK"));
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(mockResponse);
        
        ArgumentCaptor<HttpUriRequest> requestCaptor = ArgumentCaptor.forClass(HttpUriRequest.class);

        // when
        cacheService.sendRequest("http://test.com");

        // then
        verify(httpClient).execute(requestCaptor.capture());
        HttpUriRequest capturedRequest = requestCaptor.getValue();
        
        assertThat(capturedRequest.getFirstHeader("Cache-Control").getValue()).isEqualTo("no-cache");
        assertThat(capturedRequest.getFirstHeader("Origin").getValue()).isEqualTo(CACHE_CLIENT_ORIGIN);
    }
}