package com.finance.adam.service;

import com.finance.adam.util.OpenAPIUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

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
        String zipString = openAPIUtil.fetchData(url + corpCode + "?crtfc_key=" + api_key);
        String zipFilePath = openAPIUtil.ConvertStringToZipFile(zipString);
        String[] unzipFilePathList;
        try {
            unzipFilePathList = openAPIUtil.unzip(zipFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        openAPIUtil.parseXML(unzipFilePathList[0]);

        return new String[0];
    }

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
