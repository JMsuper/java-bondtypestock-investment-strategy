package com.finance.adam.util;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;

import java.lang.reflect.Field;
import java.util.Map;

@Slf4j
public class CustomModelMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    static {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public static <T> void convert(Map<String,String> snakeCaseMap, T dto , Class<T> dtoClazz){
        for(String key : snakeCaseMap.keySet()){
            String camelCaseKey = toCamelCase(key);
            try{
                Field field = dtoClazz.getDeclaredField(camelCaseKey);
                field.setAccessible(true); // private 필드 접근 허용
                field.set(dto, snakeCaseMap.get(key));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.warn(e.toString());
            }
        }
    }

    // snake_case를 camelCase로 변환하는 메서드
    private static String toCamelCase(String snakeCase) {
        StringBuilder result = new StringBuilder();
        String[] parts = snakeCase.split("_");
        for (String part : parts) {
            if (result.isEmpty()) {
                result.append(part.toLowerCase());
            } else {
                result.append(part.substring(0, 1).toUpperCase())
                        .append(part.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }
}
