package com.finance.adam.openapi.dart;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.adam.openapi.dart.dto.request.OpenDartBaseRequestDTO;
import com.finance.adam.openapi.dart.dto.response.OpenDartBaseResponseDTO;
import com.finance.adam.util.MultiValueMapConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class OpenDartUtil {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String baseUrl = "https://opendart.fss.or.kr/";

    /**
     * 분당 1,000회 이상 API 호출 시 서비스 이용 제한 규칙 적용 <br/>
     * API 요청 시 기본으로 0.2초 시간 딜레이를 갖도록 수정
     */
    private final long DELAY_MILLI_SEC = 200;

    @Value("${open-dart.service-key}")
    private String apiKey;


    public OpenDartUtil(RestTemplate restTemplate,ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        log.debug("OpenDartUtil initialized with RestTemplate and ObjectMapper");
    }

    /**
     *
     * @param path API 경로
     * @param requestDTO 요청 DTO, <br/>
     *                   DTO 의 멤버 변수 중 null 값인 것은 쿼리 파라미터 생성에서 제외, <br/>
     *                   crtfcKey 는 메서드에서 직접 지정해줌 -> so, crtfcKey 는 신경쓰지 않아도 됨
     *
     * @return 요청 성공시 List 반환, <br/>
     *         실패시 Empty List 반환
     */
    public List<Object> apiRequest(
            String path,
            OpenDartBaseRequestDTO requestDTO
    ){
        log.info("Making API request to path: {}", path);
        // 1. 매개변수 유효성 검사
        requestDTO.checkParams();
        log.debug("Parameter validation completed for request");

        // 2. API KEY 추가
        requestDTO.setCrtfcKey(apiKey);
        log.debug("API key added to request");

        // 2. URI 생성
        URI uri = createUriWithQueryParams(baseUrl + path, requestDTO);
        if(uri == null){
            log.error("Failed to create URI for path: {}", path);
            return new ArrayList<>();
        }
        log.debug("URI created successfully: {}", uri);

        // 3. API 요청
        OpenDartBaseResponseDTO responseBody = getRequest(uri);
        if(responseBody == null){
            log.error("Received null response from API request");
            return new ArrayList<>();
        }
        log.debug("Received response from API");

        // 4. 응답 상태코드 확인
        boolean isOk = checkResponseStatusCode(responseBody);
        if(!isOk){
            log.warn("Response status code check failed");
            return new ArrayList<>();
        }
        log.debug("Response status code check passed");

        List<Object> result = responseBody.getList();
        log.info("API request completed successfully. Retrieved {} items", result.size());
        return result;
    }

    /**
     * 비동기 방식으로 API 요청을 수행합니다.
     *
     * @param path API 경로
     * @param requestDTO 요청 DTO, <br/>
     *                   DTO 의 멤버 변수 중 null 값인 것은 쿼리 파라미터 생성에서 제외, <br/>
     *                   crtfcKey 는 메서드에서 직접 지정해줌 -> so, crtfcKey 는 신경쓰지 않아도 됨
     *
     * @return CompletableFuture 객체
     */
    @Async
    public CompletableFuture<List<Object>> apiRequestAsync(
            String path,
            OpenDartBaseRequestDTO requestDTO
    ){
        return CompletableFuture.completedFuture(apiRequest(path, requestDTO));
    }

    /**
     *
     * @param path 요청 URL
     * @param requestDTO 요청 Dto, crtfcKey 는 메서드에서 직접 지정해줌
     * @return 성공시 파일의 byte[], 실패 시 길이가 0인 byte[] 배열
     */
    public byte[] download(
            String path,
            OpenDartBaseRequestDTO requestDTO
    ){
        log.info("Starting download from path: {}", path);
        // 1. 매개변수 유효성 검사
        requestDTO.checkParams();
        log.debug("Parameter validation completed for download request");

        // 2. API KEY 추가
        requestDTO.setCrtfcKey(apiKey);
        log.debug("API key added to download request");

        // 2. URI 생성
        URI uri = createUriWithQueryParams(baseUrl + path, requestDTO);
        if(uri == null){
            log.error("Failed to create URI for download path: {}", path);
            return new byte[0];
        }
        log.debug("URI created successfully for download: {}", uri);

        // 3. API 요청
        ResponseEntity<byte[]> response = restTemplate.getForEntity(uri, byte[].class);
        if(response.getStatusCode() != HttpStatus.OK){
            log.warn("Download request failed with response: {}", response);
            return new byte[0];
        }

        byte[] responseBody = response.getBody();
        log.info("Download completed successfully. Downloaded {} bytes", Objects.requireNonNull(responseBody).length);
        return responseBody;
    }


    /**
     - params DTO 를 담아서, 쿼리 파라미터 형태의 URI 를 반환 <br/>
     */
    private URI createUriWithQueryParams(String requestUrl, Object params) {
        log.debug("Creating URI with query parameters for URL: {}", requestUrl);
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(requestUrl)
                .queryParams(MultiValueMapConverter.convertWithOutNull(objectMapper, params))
                .encode()
                .toUriString();
        URI uri;
        try {
            uri = new URI(urlTemplate);
            log.debug("Successfully created URI: {}", uri);
        } catch (URISyntaxException e) {
            log.error("URI creation failed for urlTemplate: {}", urlTemplate, e);
            return null;
        }
        return uri;
    }

    private OpenDartBaseResponseDTO getRequest(URI uri){
        log.debug("Making GET request to URI: {}", uri);
        try {
            log.debug("Applying delay of {} milliseconds", DELAY_MILLI_SEC);
            Thread.sleep(DELAY_MILLI_SEC);
        } catch (InterruptedException e) {
            log.debug("Sleep interrupted during request delay", e);
        }

        ResponseEntity<OpenDartBaseResponseDTO> rawResponse = restTemplate.getForEntity(uri,OpenDartBaseResponseDTO.class);
        if(rawResponse.getStatusCode() != HttpStatus.OK){
            log.warn("GET request failed with response: {}", rawResponse);
            return null;
        }
        log.debug("GET request completed successfully");
        return rawResponse.getBody();
    }

    private boolean checkResponseStatusCode(OpenDartBaseResponseDTO response){
        String statusCode = response.getStatus();
        log.debug("Checking response status code: {}", statusCode);
        try{
            OpenDartResponseMsg responseMsg = OpenDartResponseMsg.findByCode(statusCode);
            if(responseMsg != OpenDartResponseMsg.NORMAL){
                String logMgs = String.format("statusCode=%s, message=%s",statusCode,responseMsg.getMessage());

                switch (responseMsg){
                    case NO_DATA_FOUND, FILE_NOT_FOUND-> log.info(logMgs);
                    case REQUEST_LIMIT_EXCEEDED, COMPANY_LIMIT_EXCEEDED -> log.warn(logMgs);
                    default -> log.error(logMgs);
                }

                return false;
            }
            log.debug("Response status code check passed");
            return true;
        }catch (IllegalArgumentException e){
            log.error("Undefined status code received: {}", statusCode);
            return false;
        }
    }
}
