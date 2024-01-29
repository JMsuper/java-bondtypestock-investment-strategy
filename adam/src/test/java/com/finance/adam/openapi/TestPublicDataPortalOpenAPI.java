package com.finance.adam.openapi;


import com.finance.adam.openapi.vo.KrxItemInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class TestPublicDataPortalOpenAPI {

    @Autowired
    PublicDataPortalOpenAPI publicDataPortalOpenAPI;

    @Test
    void test() {
        List<KrxItemInfo> result = publicDataPortalOpenAPI.getKrxItemInfoList();
        assertNotNull(result);
    }
}
