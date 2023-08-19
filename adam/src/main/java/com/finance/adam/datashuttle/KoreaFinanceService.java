package com.finance.adam.datashuttle;

import com.finance.adam.datashuttle.OpenAPIUtil;
import com.finance.adam.datashuttle.Scrapper;
import com.finance.adam.datashuttle.FinanceServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.io.IOException;
@Service
public class KoreaFinanceService implements FinanceServiceInterface {

    @Value("${opendart.url}")
    String url;

    @Value("${opendart.api_key}")
    String api_key;

    @Value("${opendart.corp_code}")
    String corpCode;

    @Autowired
    private OpenAPIUtil openAPIUtil;
    @Autowired
    private Scrapper scrapper;

    @Override
    public Map<String, String> getStockCodeList() {
        String zipFilePath = openAPIUtil.fetchZipData(url + corpCode + "?crtfc_key=" + api_key);
        String unzipXmlFilePath;
        try {
            unzipXmlFilePath = openAPIUtil.unzip(zipFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<String,String> codeMap = openAPIUtil.parseXML(unzipXmlFilePath);

        return codeMap;
    }

    @Override
    public List getStockCodeListFromDB() {
        return null;
    }

    @Override
    public boolean updateAllFinancialInfo() {
        return false;
    }

    // 총 자본, 발행 주식수, 당기순이익
    @Override
    public Map<String, String> getFinancialData(String stockCode) {
        return scrapper.getFinancialData(stockCode);
    }

    @Override
    public List<Map<String, String>> getAllFinancialData() {
        return null;
    }
}
