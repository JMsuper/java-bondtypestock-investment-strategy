package com.finance.adam.openapi;


import com.finance.adam.openapi.vo.DataOpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URISyntaxException;

@SpringBootTest
public class TestDataOpenAPI {

    @Autowired
    DataOpenAPI dataOpenAPI;

    @Test
    void test() throws URISyntaxException {
        String responseBody = dataOpenAPI.getCorpList();
        System.out.println("responseBody = " + responseBody);
    }
}
