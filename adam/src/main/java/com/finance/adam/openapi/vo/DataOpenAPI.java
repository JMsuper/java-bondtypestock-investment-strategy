package com.finance.adam.openapi.vo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;

@Component
@Slf4j
public class DataOpenAPI {

    @Value("${data-api.corp-list-url}")
    private String corpListUrl;
    @Value("${data-api.service-key}")
    private String serviceKey;
    @Value(("${data-api.num-of-rows}"))
    private String numOfRows;

    @Autowired
    private ObjectMapper objectMapper;

    public String getCorpList() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("host","apis.data.go.kr");
        HttpEntity httpEntity = new HttpEntity(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(corpListUrl)
                .queryParam("numOfRows",numOfRows)
                .queryParam("resultType","json")
                .queryParam("serviceKey","{serviceKey}")
                .buildAndExpand(serviceKey)
                .toUriString();

        URI uri = new URI(urlTemplate);

        HttpEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET,httpEntity,String.class);
        KrxResponseHeaderVO krxResponseHeaderVO;
        KrxResponseBodyVO krxResponseBodyVO;
        try {
            HashMap<String, HashMap> rawResponse = objectMapper.readValue(response.getBody(),HashMap.class);

            krxResponseHeaderVO = objectMapper.convertValue(rawResponse.get("response").get("header"),KrxResponseHeaderVO.class);
            krxResponseBodyVO = objectMapper.convertValue(rawResponse.get("response").get("body"), KrxResponseBodyVO.class);


        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        System.out.println("urlTemplate = " + urlTemplate);
        System.out.println("uri = " + uri.toString());
        return response.getBody();
     }

}
