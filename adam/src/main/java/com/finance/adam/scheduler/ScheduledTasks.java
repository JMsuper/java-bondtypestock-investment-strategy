package com.finance.adam.scheduler;

import com.finance.adam.openapi.dart.OpenDartAPI;
import com.finance.adam.openapi.krx.CsvReaderService;
import com.finance.adam.repository.stockprice.dto.StockPriceInfoDTO;
import com.finance.adam.service.FinanceDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Map;

@Component
@Slf4j
public class ScheduledTasks {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd E요일 HH:mm:ss");

    private final CsvReaderService csvReaderService;
    private final FinanceDataService financeDataService;
    private final OpenDartAPI openDartAPI;

    public ScheduledTasks(CsvReaderService csvReaderService, FinanceDataService financeDataService, OpenDartAPI openDartAPI) {
        this.csvReaderService = csvReaderService;
        this.financeDataService = financeDataService;
        this.openDartAPI = openDartAPI;
    }

    @Scheduled(cron = "0 0,20,40 8-17 * * MON-FRI")
    @ConditionalScheduler
    public void stockPriceUpdate() {
        log.info("ScheduledTasks.stockPriceUpdate() start : {}", dateFormat.format(System.currentTimeMillis()));
        File result = csvReaderService.getKrxStockPriceCsvFile();
        String filePath = result.getPath();
        Map<String, StockPriceInfoDTO> stockPriceInfoDTOList = csvReaderService.readKrxPriceCsvFile(filePath);
        csvReaderService.saveOrUpdateStockPrices(stockPriceInfoDTOList);
        log.info("ScheduledTasks.stockPriceUpdate() end : {}", dateFormat.format(System.currentTimeMillis()));
    }

    @Scheduled(cron = "0 30 6 * * MON-FRI")
    @ConditionalScheduler
    public void stockListUpdate() {
        log.info("stockListUpdate start : {}", dateFormat.format(System.currentTimeMillis()));
        financeDataService.renewCorpInfoWithKrxList();
        log.info("stockListUpdate end : {}", dateFormat.format(System.currentTimeMillis()));
    }

    @Scheduled(cron = "0 0 7 * * MON")
    @ConditionalScheduler
    public void financeInfoUpdate() {
        log.info("financeInfoUpdate start : {}", dateFormat.format(System.currentTimeMillis()));
        financeDataService.renewFinancialInfo();
        log.info("financeInfoUpdate end : {}", dateFormat.format(System.currentTimeMillis()));
    }

    @Scheduled(cron = "0 * * * * *")
    @ConditionalScheduler
    public void recentReportRedisUpdate() {
        log.info("recentReportRedisUpdate start : {}", dateFormat.format(System.currentTimeMillis()));
        openDartAPI.updateRecentReportInRedis();
        log.info("recentReportRedisUpdate end : {}", dateFormat.format(System.currentTimeMillis()));
    }
}