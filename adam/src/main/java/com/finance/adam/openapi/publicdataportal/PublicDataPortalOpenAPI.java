package com.finance.adam.openapi.publicdataportal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.adam.openapi.publicdataportal.vo.KrxItemInfo;
import com.finance.adam.openapi.publicdataportal.vo.KrxResponseBody;
import com.finance.adam.openapi.publicdataportal.vo.KrxResponseHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 공공데이터포털 Open API
 */
@Component
@Slf4j
public class PublicDataPortalOpenAPI {

    @Value("${data-api.corp-list-url}")
    private String corpListUrl;
    @Value("${data-api.service-key}")
    private String serviceKey;
    @Value(("${data-api.num-of-rows}"))
    private String numOfRows;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    private static final String ERROR_MSG = "공공데이터포털 OpenAPI 요청에 실패하였습니다.";

    public PublicDataPortalOpenAPI(ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        log.debug("PublicDataPortalOpenAPI initialized with ObjectMapper and RestTemplate");
    }

    /**
     * 공공데이터포털 Open API를 통해 상장된 기업의 정보를 가져온다.
     * key : stockCode(ex. 005930)
     */
    public Map<String,KrxItemInfo> getKrxItemInfoMap() {
        log.debug("Starting to fetch KRX item information");
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);
        log.debug("HTTP headers set with Accept: APPLICATION_JSON");

        URI uri;
        try {
            uri = createURI();
            log.debug("Created URI for API request: {}", uri);
        } catch (URISyntaxException e) {
            log.error("Failed to create URI", e);
            throw new RuntimeException(ERROR_MSG,e);
        }

        log.debug("Making HTTP request to KRX API");
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET,httpEntity,String.class);
        KrxResponseHeader krxResponseHeader;
        KrxResponseBody krxResponseBody;

        if(response.getStatusCode() != HttpStatus.OK){
            log.error("API request failed with status: {}", response.getStatusCode());
            log.warn(response.toString());
            throw new RuntimeException(ERROR_MSG);
        }
        log.debug("Received successful response from KRX API");

        try {
            HashMap<String, HashMap> rawResponse = objectMapper.readValue(response.getBody(),HashMap.class);
            krxResponseHeader = objectMapper.convertValue(rawResponse.get("response").get("header"),KrxResponseHeader.class);
            krxResponseBody = objectMapper.convertValue(rawResponse.get("response").get("body"), KrxResponseBody.class);
            log.debug("Successfully parsed API response");
        } catch (JsonProcessingException e) {
            log.error("Failed to parse API response", e);
            throw new RuntimeException(ERROR_MSG,e);
        }

        if(krxResponseHeader.getResultCode().equals("00")){
            log.info("API request successful: {}", krxResponseHeader);
        }else{
            log.warn("API request returned error: {}", krxResponseHeader);
            throw new RuntimeException(ERROR_MSG);
        }

        List<KrxItemInfo> listOfDuplicatedData = krxResponseBody.getItems();
        log.debug("Retrieved {} items from API (may include duplicates)", listOfDuplicatedData.size());
        
        Map<String, KrxItemInfo> distinctMap = new HashMap<>();
        for (KrxItemInfo krxItemInfo : listOfDuplicatedData) {
            distinctMap.put(krxItemInfo.getSrtnCd(), krxItemInfo);
        }
        log.info("Successfully processed {} unique KRX items", distinctMap.size());
        return distinctMap;
    }

    /**
     * 2024-04-17 기준, 거래소 상장 종목의 총 개수는 약 2,600개 이다.
     * numOfRows 가 총 개수보다 클 경우, 중복된 종목을 추가하여 numOfRows 만큼의 데이터를 반환한다.
     * 즉, 데이터 중복이 존재한다.
     */
    private URI createURI() throws URISyntaxException {
        log.debug("Creating URI with parameters - numOfRows: {}", numOfRows);
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(corpListUrl)
                .queryParam("numOfRows",numOfRows)
                .queryParam("resultType","json")
                .queryParam("serviceKey","{serviceKey}")
                .buildAndExpand(serviceKey)
                .toUriString();

        return new URI(urlTemplate);
    }

}
