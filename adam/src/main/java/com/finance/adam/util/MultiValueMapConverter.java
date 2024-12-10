package com.finance.adam.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Slf4j
public class MultiValueMapConverter {

    private MultiValueMapConverter(){}

    /**
     * Object 의 멤버 변수 중 null 값인 것도 포함
     */
    public static MultiValueMap<String, String> convert(ObjectMapper objectMapper, Object dto){
        try{
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            Map<String, String> map = objectMapper.convertValue(dto, new TypeReference<>() {
            });
            params.setAll(map);

            return params;
        }catch (Exception e){
            log.error("Uri Parameter 변환 중 오류가 발생하였습니다. requestDto={}",dto, e);
            throw new IllegalStateException("Url Parameter 변환 중 오류가 발생하였습니다.");
        }
    }

    /**
     * Object 의 멤버 변수 중 null 값인 것은 제외
     */
    public static MultiValueMap<String, String> convertWithOutNull(ObjectMapper objectMapper, Object dto){
        try{
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            Map<String, String> map = objectMapper.convertValue(dto, new TypeReference<>() {
            });
            map.entrySet().stream()
                    .filter(entry -> entry.getValue() != null)
                    .forEach(entry -> params.add(entry.getKey(), entry.getValue()));

            return params;
        }catch (Exception e){
            log.error("Uri Parameter 변환 중 오류가 발생하였습니다. requestDto={}",dto, e);
            throw new IllegalStateException("Url Parameter 변환 중 오류가 발생하였습니다.");
        }
    }
}
