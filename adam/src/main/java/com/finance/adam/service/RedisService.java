package com.finance.adam.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
        log.debug("RedisService initialized with RedisTemplate");
    }

    // Redis String 데이터 저장
    public void saveData(String key, Object value) {
        log.info("Saving data to Redis - key: {}", key);
        redisTemplate.opsForValue().set(key, value);
        log.debug("Successfully saved data for key: {}", key);
    }

    // Redis String 데이터 조회
    public Object getData(String key) {
        log.info("Getting data from Redis - key: {}", key);
        Object value = redisTemplate.opsForValue().get(key);
        log.debug("Retrieved data for key: {}, exists: {}", key, (value != null));
        return value;
    }

    /**
     * 특정 키가 Redis에 존재하는지 확인
     * @param key 확인할 키
     * @return 키 존재 여부 (true: 존재, false: 존재하지 않음)
     */
    public boolean isKeyExists(String key) {
        log.info("Checking if key exists in Redis - key: {}", key);
        boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(key));
        log.debug("Key {} exists: {}", key, exists);
        return exists;
    }

    // Redis List: LPUSH (List 앞쪽에 데이터 추가)
    public void pushToList(String key, Object value) {
        log.info("Pushing data to Redis list - key: {}", key);
        redisTemplate.opsForList().leftPush(key, value);
        log.debug("Successfully pushed data to list for key: {}", key);
    }

    // Redis List: LTRIM (List 길이 제한)
    public void trimList(String key, int start, int end) {
        log.info("Trimming Redis list - key: {}, start: {}, end: {}", key, start, end);
        redisTemplate.opsForList().trim(key, start, end);
        log.debug("Successfully trimmed list for key: {}", key);
    }

    // Redis List: LRANGE (List 범위 조회)
    public List<Object> getListRange(String key, int start, int end) {
        log.info("Getting range from Redis list - key: {}, start: {}, end: {}", key, start, end);
        List<Object> result = redisTemplate.opsForList().range(key, start, end);
        log.debug("Retrieved {} items from list for key: {}", result != null ? result.size() : 0, key);
        return result;
    }

    // 특정 키 삭제
    public void deleteKey(String key) {
        log.info("Deleting key from Redis - key: {}", key);
        redisTemplate.delete(key);
        log.debug("Successfully deleted key: {}", key);
    }
}
