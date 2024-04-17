package com.finance.adam.openapi;


import com.finance.adam.openapi.publicdataportal.PublicDataPortalOpenAPI;
import com.finance.adam.openapi.publicdataportal.vo.KrxItemInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class TestPublicDataPortalOpenAPI {

    @Autowired
    PublicDataPortalOpenAPI publicDataPortalOpenAPI;

}
