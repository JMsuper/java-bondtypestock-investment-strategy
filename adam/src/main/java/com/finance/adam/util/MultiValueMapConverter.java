package com.finance.adam.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Type;
import java.util.Map;

@Slf4j
public abstract class MultiValueMapConverter {

    private MultiValueMapConverter(){}

    public static MultiValueMap<String, String> convert(ObjectMapper objectMapper, Object dto){
        try{
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            Map<String, String> map = objectMapper.convertValue(dto, new TypeReference<Map<String, String>>() {});
            params.setAll(map);

            return params;
        }catch (Exception e){
            log.error("Uri Parameter 변환 중 오류가 발생하였습니다. requestDto={}",dto, e);
            throw new IllegalStateException("Url Parameter 변환 중 오류가 발생하였습니다.");
        }
    }
}
