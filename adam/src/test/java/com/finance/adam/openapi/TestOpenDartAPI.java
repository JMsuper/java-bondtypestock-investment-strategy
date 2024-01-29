package com.finance.adam.openapi;

import com.finance.adam.openapi.dart.OpenDartAPI;
import com.finance.adam.openapi.dart.vo.OpenDartFinancialInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TestOpenDartAPI {

    @Autowired
    OpenDartAPI openDartAPI;

    @Test
    void test(){

        String corpCode = "00126380";
        String bsnsYear = "2022";

        List<OpenDartFinancialInfo> result = openDartAPI.getCorpFinancialInfo(corpCode,bsnsYear);
        for (OpenDartFinancialInfo info : result){
            System.out.println(info);
        }
    }
}
