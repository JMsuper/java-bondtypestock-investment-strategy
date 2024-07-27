package com.finance.adam.openapi;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.adam.openapi.publicdataportal.PublicDataPortalOpenAPI;
import com.finance.adam.openapi.publicdataportal.vo.KrxItemInfo;
import com.finance.adam.openapi.publicdataportal.vo.KrxResponseBody;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {
        PublicDataPortalOpenAPI.class, ObjectMapper.class, RestTemplate.class
})
public class TestPublicDataPortalOpenAPI {

    @Autowired
    PublicDataPortalOpenAPI publicDataPortalOpenAPI;

    @Test
    void getKrxItemInfoMap(){
        Map<String, KrxItemInfo> map = publicDataPortalOpenAPI.getKrxItemInfoMap();
        System.out.println();
    }

}
