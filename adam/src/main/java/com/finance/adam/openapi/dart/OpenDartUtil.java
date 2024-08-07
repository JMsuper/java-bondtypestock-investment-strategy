package com.finance.adam.openapi.dart;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.adam.openapi.dart.dto.request.OpenDartBaseRequestDTO;
import com.finance.adam.openapi.dart.dto.response.OpenDartBaseResponseDTO;
import com.finance.adam.util.MultiValueMapConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
        // 1. 매개변수 유효성 검사
        requestDTO.checkParams();

        // 2. API KEY 추가
        requestDTO.setCrtfcKey(apiKey);

        // 2. URI 생성
        URI uri = createUriWithQueryParams(baseUrl + path, requestDTO);
        if(uri == null){
            return new ArrayList<>();
        }

        // 3. API 요청
        OpenDartBaseResponseDTO responseBody = getRequest(uri);
        if(responseBody == null){
            return new ArrayList<>();
        }

        // 4. 응답 상태코드 확인
        boolean isOk = checkResponseStatusCode(responseBody);
        if(!isOk){
            return new ArrayList<>();
        }

        return responseBody.getList();
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
        // 1. 매개변수 유효성 검사
        requestDTO.checkParams();

        // 2. API KEY 추가
        requestDTO.setCrtfcKey(apiKey);

        // 2. URI 생성
        URI uri = createUriWithQueryParams(baseUrl + path, requestDTO);
        if(uri == null){
            return new byte[0];
        }

        // 3. API 요청
        ResponseEntity<byte[]> response = restTemplate.getForEntity(uri, byte[].class);
        if(response.getStatusCode() != HttpStatus.OK){
            log.warn(response.toString());
            return new byte[0];
        }

        return response.getBody();
    }


    /**
     - params DTO 를 담아서, 쿼리 파라미터 형태의 URI 를 반환 <br/>
     */
    private URI createUriWithQueryParams(String requestUrl, Object params) {
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(requestUrl)
                .queryParams(MultiValueMapConverter.convertWithOutNull(objectMapper, params))
                .encode()
                .toUriString();
        URI uri;
        try {
            uri = new URI(urlTemplate);
        } catch (URISyntaxException e) {
            log.error("URI 생성 중 오류가 발생하였습니다. urlTemplate={}", urlTemplate, e);
            return null;
        }
        return uri;
    }

    private OpenDartBaseResponseDTO getRequest(URI uri){
        if(DELAY_MILLI_SEC != 0){
            try {
                Thread.sleep(DELAY_MILLI_SEC);
            } catch (InterruptedException e) {}
        }

        ResponseEntity<OpenDartBaseResponseDTO> rawResponse = restTemplate.getForEntity(uri,OpenDartBaseResponseDTO.class);
        if(rawResponse.getStatusCode() != HttpStatus.OK){
            log.warn(rawResponse.toString());
            return null;
        }
        return rawResponse.getBody();
    }

    private boolean checkResponseStatusCode(OpenDartBaseResponseDTO response){
        String statusCode = response.getStatus();
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
        }catch (IllegalArgumentException e){
            log.error("정의되지 않은 상태 코드입니다. statusCode={}",statusCode);
            return false;
        }
        return true;
    }
}
