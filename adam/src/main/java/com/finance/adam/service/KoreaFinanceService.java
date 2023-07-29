package com.finance.adam.service;

import com.finance.adam.util.OpenAPIUtil;
import com.finance.adam.util.Scrapper;
import org.springframework.beans.factory.annotation.Autowired;
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

//    public void saveStockListToDB(){
//        Connection connection = dataSource.getConnection();
//        Map<String,String> codeList = koreaFinanceService.getStockCodeList();
//        for(String key : codeList.keySet()){
//            String code = key;
//            String name = codeList.get(code);
//            String sql = "insert into corp_code(code,name) values("+code+",\""+name+"\");";
//            jdbcTemplate.execute(sql);
//        }
//    }

    // 총 자본, 발행 주식수, 당기순이익
    @Override
    public Map<String, String> getFinancialData(String stockCode) {
        return scrapper.getFinancialData(stockCode);
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
