package com.finance.adam.service;

import com.finance.adam.repository.stockprice.dto.StockPriceInfoDTO;
import com.finance.adam.openapi.krx.CsvReaderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.Map;

@SpringBootTest
class CsvReaderServiceTest {

    @Autowired
    CsvReaderService csvReaderService;

//    @Test
//    void test1(){
//        File result = csvReaderService.getKrxStockPriceCsvFile();
//        String filePath = result.getPath();
//        Map<String, StockPriceInfoDTO> stockPriceInfoDTOList = csvReaderService.readKrxPriceCsvFile(filePath);
//        csvReaderService.saveOrUpdateStockPrices(stockPriceInfoDTOList);
//    }
//
//    @Test
//    void test2(){
//        File result = csvReaderService.getKrxStockPriceCsvFile();
//        String filePath = result.getPath();
//        Map<String, StockPriceInfoDTO> stockPriceInfoDTOList = csvReaderService.readKrxPriceCsvFile(filePath);
//    }
//
//    @Test
//    void test3(){
//        String code = csvReaderService.getKrxStockPriceOTPCode();
//        System.out.println(code);
//    }


}