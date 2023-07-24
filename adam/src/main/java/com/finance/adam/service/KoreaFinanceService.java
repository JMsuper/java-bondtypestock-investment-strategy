package com.finance.adam.service;

import com.finance.adam.util.OpenAPIUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.io.IOException;
@Service
public class KoreaFinanceService implements FinanceServiceInterface{

    @Value("${opendart.url}")
    String url;

    @Value("${opendart.api_key}")
    String api_key;

    @Value("${opendart.corp_code}")
    String corpCode;

//    @Autowired
//    private OpenAPIUtil openAPIUtil;

    @Override
    public String[] getStockCodeList() {
        OpenAPIUtil openAPIUtil = new OpenAPIUtil();
        String zipFilePath = openAPIUtil.fetchZipData(url + corpCode + "?crtfc_key=" + api_key);
        String unzipXmlFilePath;
        try {
            unzipXmlFilePath = openAPIUtil.unzip(zipFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        openAPIUtil.parseXML(unzipXmlFilePath);

        return new String[0];
    }

    // 총 자본, 발행 주식수, 당기순이익
    @Override
    public Map<String, Integer> getFinancialData(String stockCode) {
        return null;
    }

    @Override
    public int getStockPrice(String stockCode) {
        return 0;
    }

    @Override
    public boolean isValidToBuy(String stockCode) {
        return false;
    }
}
