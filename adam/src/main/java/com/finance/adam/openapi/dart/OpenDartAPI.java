package com.finance.adam.openapi.dart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.adam.openapi.dart.vo.OpenDartFinancialInfo;
import com.finance.adam.openapi.dart.vo.OpenDartFinancialInfoRequest;
import com.finance.adam.openapi.dart.vo.OpenDartFinancialInfoResponse;
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
import java.util.List;

@Component
@Slf4j
public class OpenDartAPI {

    @Value("${open-dart.financial-info-url}")
    private String url;
    @Value("${open-dart.service-key}")
    private String serviceKey;
    @Value("${open-dart.report-code}")
    private String reprtCode;

    private final String ERROR_MSG = "재무정보를 가져오는데 실패하였습니다.";

    private ObjectMapper objectMapper;

    public OpenDartAPI(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    public List<OpenDartFinancialInfo> getCorpFinancialInfo(String corpCode, String bsnsYear){
        RestTemplate restTemplate = new RestTemplate();

        OpenDartFinancialInfoRequest params = OpenDartFinancialInfoRequest.builder()
                .crtfcKey(serviceKey)
                .corpCode(corpCode)
                .bsnsYear(bsnsYear)
                .reprtCode(reprtCode)
                .build();

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParams(MultiValueMapConverter.convert(objectMapper, params))
                .encode()
                .toUriString();
        URI uri;
        try {
            uri = new URI(urlTemplate);
        } catch (URISyntaxException e) {
            throw new RuntimeException("URI 생성 중 오류가 발생하였습니다.",e);
        }

        ResponseEntity<String> rawResponse = restTemplate.getForEntity(uri,String.class);
        if(rawResponse.getStatusCode() != HttpStatus.OK){
            log.warn(rawResponse.toString());
            throw new RuntimeException(ERROR_MSG);
        }

        OpenDartFinancialInfoResponse response;
        try {
            response =  objectMapper.readValue(rawResponse.getBody(), OpenDartFinancialInfoResponse.class);
        } catch (JsonProcessingException e) {
            log.warn(rawResponse.toString());
            throw new RuntimeException(ERROR_MSG,e);
        }

        if(!response.getStatus().equals("000")){
            log.warn(response.toString());
            throw new RuntimeException(ERROR_MSG);
        }

        List<OpenDartFinancialInfo> financialInfoList = response.getList();
        return financialInfoList;
    }
}
