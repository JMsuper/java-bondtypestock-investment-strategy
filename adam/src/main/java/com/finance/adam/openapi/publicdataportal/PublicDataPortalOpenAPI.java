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

    private ObjectMapper objectMapper;

    private static final String ERROR_MSG = "공공데이터포털 OpenAPI 요청에 실패하였습니다.";

    public PublicDataPortalOpenAPI(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public List<KrxItemInfo> getKrxItemInfoList() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        URI uri;
        try {
            uri = createURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(ERROR_MSG,e);
        }

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET,httpEntity,String.class);
        KrxResponseHeader krxResponseHeader;
        KrxResponseBody krxResponseBody;

        if(response.getStatusCode() != HttpStatus.OK){
            log.warn(response.toString());
            throw new RuntimeException(ERROR_MSG);
        }

        try {
            HashMap<String, HashMap> rawResponse = objectMapper.readValue(response.getBody(),HashMap.class);
            krxResponseHeader = objectMapper.convertValue(rawResponse.get("response").get("header"),KrxResponseHeader.class);
            krxResponseBody = objectMapper.convertValue(rawResponse.get("response").get("body"), KrxResponseBody.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(ERROR_MSG,e);
        }

        if(krxResponseHeader.getResultCode().equals("00")){
            log.info(krxResponseHeader.toString());
        }else{
            log.warn(krxResponseHeader.toString());
            throw new RuntimeException(ERROR_MSG);
        }

        return krxResponseBody.getItems();
    }

    private URI createURI() throws URISyntaxException {
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(corpListUrl)
                .queryParam("numOfRows",numOfRows)
                .queryParam("resultType","json")
                .queryParam("serviceKey","{serviceKey}")
                .buildAndExpand(serviceKey)
                .toUriString();

        return new URI(urlTemplate);
    }

}
