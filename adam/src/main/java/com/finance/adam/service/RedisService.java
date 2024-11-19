package com.finance.adam.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedisService {

    private RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    // Redis String 데이터 저장
    public void saveData(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // Redis String 데이터 조회
    public Object getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 특정 키가 Redis에 존재하는지 확인
     * @param key 확인할 키
     * @return 키 존재 여부 (true: 존재, false: 존재하지 않음)
     */
    public boolean isKeyExists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // Redis List: LPUSH (List 앞쪽에 데이터 추가)
    public void pushToList(String key, Object value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    // Redis List: LTRIM (List 길이 제한)
    public void trimList(String key, int start, int end) {
        redisTemplate.opsForList().trim(key, start, end);
    }

    // Redis List: LRANGE (List 범위 조회)
    public List<Object> getListRange(String key, int start, int end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    // 특정 키 삭제
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }
}

