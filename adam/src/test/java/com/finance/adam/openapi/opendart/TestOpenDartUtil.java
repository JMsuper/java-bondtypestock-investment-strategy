package com.finance.adam.openapi.opendart;

import com.finance.adam.openapi.dart.dto.request.OpenDartBaseRequestDTO;
import com.finance.adam.openapi.dart.dto.response.OpenDartBaseResponseDTO;
import com.finance.adam.openapi.dart.OpenDartResponseMsg;
import com.finance.adam.openapi.dart.OpenDartUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TestOpenDartUtil {

    @Autowired
    private OpenDartUtil openDartUtil;

    @Test
    public void apiRequest (){
        // given
        String path = "api/fnlttSinglAcnt.json";
        OpenDartBaseRequestDTO requestDTO = OpenDartBaseRequestDTO.builder()
                .corpCode("00126380")
                .bsnsYear("2018")
                .reprtCode("11011")
                .build();

        // when
        List<Object> result = openDartUtil.apiRequest(path, requestDTO);

        // then
        assertNotNull(result);
    }

    @Test
    public void createUriWithQueryParams() {
        // given
        String path = "https://opendart.fss.or.kr/api/fnlttSinglAcnt.json";
        OpenDartBaseRequestDTO requestDTO = OpenDartBaseRequestDTO.builder()
                .corpCode("00126380")
                .bsnsYear("2018")
                .reprtCode("11011")
                .build();

        // when
        URI uri = ReflectionTestUtils.invokeMethod(openDartUtil, "createUriWithQueryParams", path, requestDTO);

        // then
        assertEquals("https://opendart.fss.or.kr/api/fnlttSinglAcnt.json?corp_code=00126380&bsns_year=2018&reprt_code=11011", uri.toString());
    }

    @Test
    public void getRequest_200_OK(){
        // given
        RestTemplate mockRestTemplate = Mockito.mock(RestTemplate.class);
        OpenDartBaseResponseDTO responseDTO = new OpenDartBaseResponseDTO();
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(responseDTO);

        Mockito.when(mockRestTemplate.getForEntity(Mockito.any(URI.class),Mockito.any()))
                .thenReturn(responseEntity);
        ReflectionTestUtils.setField(openDartUtil, "restTemplate", mockRestTemplate);

        // when
        OpenDartBaseResponseDTO result = ReflectionTestUtils.invokeMethod(openDartUtil, "getRequest", URI.create("https://opendart.fss.or.kr/api/fnlttSinglAcnt.json?corp_code=00126380&bsns_year=2018&reprt_code=11011"));

        // then
        assertNotNull(result);
    }

    @Test
    public void getRequest_ERROR(){
        // given
        RestTemplate mockRestTemplate = Mockito.mock(RestTemplate.class);
        OpenDartBaseResponseDTO responseDTO = new OpenDartBaseResponseDTO();
        ResponseEntity<Object> responseEntity = ResponseEntity.status(500).build();

        Mockito.when(mockRestTemplate.getForEntity(Mockito.any(URI.class),Mockito.any()))
                .thenReturn(responseEntity);
        ReflectionTestUtils.setField(openDartUtil, "restTemplate", mockRestTemplate);

        // when
        OpenDartBaseResponseDTO result = ReflectionTestUtils.invokeMethod(openDartUtil, "getRequest", URI.create("https://opendart.fss.or.kr/api/fnlttSinglAcnt.json?corp_code=00126380&bsns_year=2018&reprt_code=11011"));

        // then
        assertNull(result);
    }


    @Test
    public void checkResponseStatusCode() {
        // given
        OpenDartBaseResponseDTO responseDTO = new OpenDartBaseResponseDTO();
        responseDTO.setStatus("000");

        boolean result = ReflectionTestUtils.invokeMethod(openDartUtil, "checkResponseStatusCode", responseDTO);
        assertTrue(result);

        for(OpenDartResponseMsg msg : OpenDartResponseMsg.values()){
            if(msg == OpenDartResponseMsg.NORMAL) continue;
            String statusCode = msg.getCode();
            responseDTO.setStatus(statusCode);
            result = ReflectionTestUtils.invokeMethod(openDartUtil, "checkResponseStatusCode", responseDTO);
            assertFalse(result);
        }

        responseDTO.setStatus("999");
        result = ReflectionTestUtils.invokeMethod(openDartUtil, "checkResponseStatusCode", responseDTO);
        assertFalse(result);
    }
}
