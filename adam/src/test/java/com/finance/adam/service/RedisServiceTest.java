package com.finance.adam.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    private final String stringKey = "testStringKey";
    private final String listKey = "testListKey";

    @BeforeEach
    public void setUp() {
        // 테스트 시작 전에 Redis 초기화
        redisService.deleteKey(stringKey);
        redisService.deleteKey(listKey);
    }

    @AfterEach
    public void tearDown() {
        // 테스트 종료 후 Redis 데이터 정리
        redisService.deleteKey(stringKey);
        redisService.deleteKey(listKey);
    }

    @Test
    public void testSaveAndGetStringData() {
        // 테스트 데이터
        String testValue = "Hello, Redis!";

        // 데이터 저장
        redisService.saveData(stringKey, testValue);

        // 데이터 조회 및 검증
        Object retrievedValue = redisService.getData(stringKey);
        assertNotNull(retrievedValue);
        assertEquals(testValue, retrievedValue);
    }

    @Test
    public void testPushToListAndGetRange() {
        // 테스트 데이터
        String value1 = "Item1";
        String value2 = "Item2";
        String value3 = "Item3";

        // List에 데이터 추가
        redisService.pushToList(listKey, value1);
        redisService.pushToList(listKey, value2);
        redisService.pushToList(listKey, value3);

        // List 조회 및 검증
        List<Object> list = redisService.getListRange(listKey, 0, -1);
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals(value3, list.get(0)); // LPUSH로 인해 마지막 추가된 값이 첫 번째
        assertEquals(value2, list.get(1));
        assertEquals(value1, list.get(2));
    }

    @Test
    public void testTrimList() {
        // 테스트 데이터
        String value1 = "Item1";
        String value2 = "Item2";
        String value3 = "Item3";
        String value4 = "Item4";

        // List에 데이터 추가
        redisService.pushToList(listKey, value1);
        redisService.pushToList(listKey, value2);
        redisService.pushToList(listKey, value3);
        redisService.pushToList(listKey, value4);

        // List 길이 제한
        redisService.trimList(listKey, 0, 2);

        // List 조회 및 검증
        List<Object> trimmedList = redisService.getListRange(listKey, 0, -1);
        assertNotNull(trimmedList);
        assertEquals(3, trimmedList.size()); // 0 ~ 2 인덱스까지만 유지
        assertEquals(value4, trimmedList.get(0)); // 최신 데이터가 첫 번째
        assertEquals(value3, trimmedList.get(1));
        assertEquals(value2, trimmedList.get(2));
    }

    @Test
    public void testDeleteKey() {
        // 테스트 데이터
        String testValue = "Hello, Redis!";

        // 데이터 저장
        redisService.saveData(stringKey, testValue);

        // 데이터 삭제
        redisService.deleteKey(stringKey);

        // 데이터 조회 및 검증
        Object retrievedValue = redisService.getData(stringKey);
        assertNull(retrievedValue); // 삭제되었으므로 null이어야 함
    }
}

