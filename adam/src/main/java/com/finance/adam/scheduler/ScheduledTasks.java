package com.finance.adam.scheduler;

import com.finance.adam.openapi.dart.OpenDartAPI;
import com.finance.adam.openapi.dart.dto.DartReportDTO;
import com.finance.adam.openapi.krx.CsvReaderService;
import com.finance.adam.repository.notification.domain.Notification;
import com.finance.adam.repository.reportalarm.domain.ReportType;
import com.finance.adam.repository.stockprice.StockPriceRepository;
import com.finance.adam.repository.stockprice.dto.StockPriceInfoDTO;
import com.finance.adam.service.FinanceDataService;
import com.finance.adam.service.AlarmCheckService;
import com.finance.adam.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class ScheduledTasks {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd E요일 HH:mm:ss");

    private final CsvReaderService csvReaderService;
    private final FinanceDataService financeDataService;
    private final OpenDartAPI openDartAPI;
    private final AlarmCheckService alarmCheckService;
    private final NotificationService notificationService;
    private final StockPriceRepository stockPriceRepository;

    @Scheduled(cron = "0 0,10,20,30,40,50 * * * *")
    @ConditionalScheduler
    public void stockPriceUpdate() {
        log.info("ScheduledTasks.stockPriceUpdate() start : {}", dateFormat.format(System.currentTimeMillis()));
        LocalTime localTime = LocalTime.now();

        File result = csvReaderService.getKrxStockPriceCsvFile();
        String filePath = result.getPath();

        Map<String, StockPriceInfoDTO> stockPriceInfoDTOMap;
        try{
            stockPriceInfoDTOMap  = csvReaderService.readKrxPriceCsvFile(filePath);
            csvReaderService.saveOrUpdateStockPrices(stockPriceInfoDTOMap);
        }catch (NumberFormatException e) {
            // 주가 갱신 실패했을 때도 주가 알림은 수행되야함
            log.info("CSV file에서 주가정보 숫자 변환 실패 - 00:00 ~ 09:10 <- 인지된 사항");
            stockPriceInfoDTOMap =stockPriceRepository.findAllWithCorpInfo()
                    .stream().map(StockPriceInfoDTO::from)
                    .collect(Collectors.toMap(
                            StockPriceInfoDTO::getStockCode,
                            Function.identity()));
        }

        // 목표가 알림
        List<Notification> targetPriceNotifications = alarmCheckService.triggerTargetPriceAlarm(stockPriceInfoDTOMap);
        targetPriceNotifications.forEach(notificationService::handleNotification);

        // 주가 정기 알림
        // java.util.DayOfWeek(월:1, 화:2) 와 현 서비스 기준(월:0, 화:1)을 맞추기 위함
        int dayOfWeek = LocalDate.now().getDayOfWeek().getValue() - 1;
        List<Notification> priceNotifications = alarmCheckService.triggerStockPriceAlarm(
                stockPriceInfoDTOMap,
                localTime,
                dayOfWeek
        );
        priceNotifications.forEach(notificationService::handleNotification);

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

        HashMap<ReportType, List<DartReportDTO>> reportTypeMap = openDartAPI.updateRecentReportInRedis();
        // 공시 알림
        List<Notification> notifications = alarmCheckService.triggerReportAlarm(reportTypeMap);
        notifications.forEach(notificationService::handleNotification);

        log.info("recentReportRedisUpdate end : {}", dateFormat.format(System.currentTimeMillis()));
    }
}