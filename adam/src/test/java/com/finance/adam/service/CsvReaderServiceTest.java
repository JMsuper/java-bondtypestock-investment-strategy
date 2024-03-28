package com.finance.adam.service;

import com.finance.adam.dto.StockPriceInfoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CsvReaderServiceTest {

    @Autowired
    CsvReaderService csvReaderService;

    @Test
    void test1(){
        File result = csvReaderService.getKrxStockPriceCsvFile();
        String filePath = result.getPath();
        List<StockPriceInfoDTO> stockPriceInfoDTOList = csvReaderService.readKrxPriceCsvFile(filePath);
        csvReaderService.saveOrUpdateStockPrices(stockPriceInfoDTOList);
    }


}