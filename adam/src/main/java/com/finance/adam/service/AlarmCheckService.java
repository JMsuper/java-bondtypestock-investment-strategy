package com.finance.adam.service;

import com.finance.adam.openapi.dart.dto.DartReportDTO;
import com.finance.adam.repository.account.domain.Account;
import com.finance.adam.repository.notification.NotificationRepository;
import com.finance.adam.repository.notification.domain.Notification;
import com.finance.adam.repository.pricealarm.PriceAlarmRepository;
import com.finance.adam.repository.pricealarm.domain.PriceAlarm;
import com.finance.adam.repository.reportalarm.ReportAlarmRepository;
import com.finance.adam.repository.reportalarm.domain.ReportAlarm;
import com.finance.adam.repository.reportalarm.domain.ReportType;
import com.finance.adam.repository.savecorpinfo.domain.SaveCorpInfo;
import com.finance.adam.repository.stockprice.dto.StockPriceInfoDTO;
import com.finance.adam.repository.targetpricealarm.TargetPriceAlarmRepository;
import com.finance.adam.repository.targetpricealarm.domain.TargetPriceAlarm;
import com.finance.adam.util.HtmlBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AlarmCheckService {

    private static final String REPORT_ALARM_TYPE = "reportalarm";
    private static final String PRICE_ALARM_TYPE = "pricealarm";
    private static final String TARGET_PRICE_ALARM_TYPE = "targetpricealarm";

    private final TargetPriceAlarmRepository targetPriceAlarmRepository;
    private final PriceAlarmRepository priceAlarmRepository;
    private final ReportAlarmRepository reportAlarmRepository;
    private final NotificationRepository notificationRepository;
    private final HtmlBuilder htmlBuilder;

    /**
     * 공시 알람을 트리거하여 알림을 생성합니다.
     *
     * @param reportTypeMap 공시유형과 기업코드 리스트를 담은 맵
     * @return 생성된 알림 리스트
     */
    public List<Notification> triggerReportAlarm(HashMap<ReportType, List<DartReportDTO>> reportTypeMap) {
        if (CollectionUtils.isEmpty(reportTypeMap)) {
            log.warn("Empty reportTypeMap provided");
            return new ArrayList<>();
        }

        HashMap<DartReportDTO, List<SaveCorpInfo>> resultMap = new HashMap<>();

        for (Map.Entry<ReportType, List<DartReportDTO>> entry : reportTypeMap.entrySet()) {
            ReportType reportType = entry.getKey();
            List<DartReportDTO> dartReportDTOList = entry.getValue();

            for (DartReportDTO dartReportDTO : dartReportDTOList) {
                String corpCode = dartReportDTO.getCorpCode();

                if (!StringUtils.hasText(corpCode)) {
                    log.warn("Empty corpCode in DartReportDTO: {}", dartReportDTO);
                    continue;
                }

                // 해당 corpCode에 대한 ReportAlarm 조회
                List<ReportAlarm> reportAlarms = reportAlarmRepository.findAllByReportTypeAndCorpCode(
                        reportType, corpCode
                );

                // SaveCorpInfo 리스트를 DartReportDTO와 매핑
                resultMap.put(dartReportDTO, reportAlarms.stream()
                        .map(ReportAlarm::getSaveCorpInfo)
                        .collect(Collectors.toList()));
            }
        }

        // DartReportDTO와 관련된 SaveCorpInfo로 Notification 생성
        List<Notification> notifications = createReportNotifications(resultMap);
        notificationRepository.saveAll(notifications);

        return notifications;
    }


    /**
     * 주가 정기 알람을 트리거하여 알림을 생성합니다.
     * @param stockPriceInfoMap 주가 정보 맵
     * @return 생성된 알림 리스트
     */
    public List<Notification> triggerStockPriceAlarm(Map<String, StockPriceInfoDTO> stockPriceInfoMap, LocalTime now, int currentDayOfWeek) {
        if (CollectionUtils.isEmpty(stockPriceInfoMap)) {
            log.warn("Stock price alarm trigger failed: empty stockPriceInfoMap");
            return new ArrayList<>();
        }

        List<PriceAlarm> matchingAlarms = priceAlarmRepository.findAllByActive(true).stream()
                .filter(alarm -> isAlarmTimeMatching(alarm, now, currentDayOfWeek))
                .collect(Collectors.toList());

        log.debug("Found {} matching price alarms for current time: {}", matchingAlarms.size(), now);
        return createPriceAlarmNotifications(matchingAlarms, stockPriceInfoMap);
    }

    /**
     * 목표가 알람을 트리거하여 알림을 생성합니다.
     * @param stockPriceInfoMap 주가 정보 맵
     * @return 생성된 알림 리스트
     */
    public List<Notification> triggerTargetPriceAlarm(Map<String, StockPriceInfoDTO> stockPriceInfoMap) {
        if (CollectionUtils.isEmpty(stockPriceInfoMap)) {
            log.warn("Empty stockPriceInfoMap provided");
            return new ArrayList<>();
        }

        List<TargetPriceAlarm> targetPriceAlarms = targetPriceAlarmRepository.findAllByActiveAndAlarmed(true, false);
        
        if (CollectionUtils.isEmpty(targetPriceAlarms)) {
            return new ArrayList<>();
        }

        Map<String, List<TargetPriceAlarm>> targetPriceAlarmMap = targetPriceAlarms.stream()
                .collect(Collectors.groupingBy(alarm -> 
                    alarm.getSaveCorpInfo().getCorpInfo().getStockCode()));

        return processTargetPriceAlarms(targetPriceAlarmMap, stockPriceInfoMap);
    }

    private List<Notification> createReportNotifications(HashMap<DartReportDTO, List<SaveCorpInfo>> resultMap) {
        return resultMap.entrySet().stream()
            .flatMap(entry -> entry.getValue().stream()
                .map(saveCorpInfo -> {
                    String corpName = saveCorpInfo.getCorpInfo().getName();
                    String reportName = entry.getKey().getReportNm();
                    String subject = String.format("[%s] %s 공시", corpName, reportName);
                    return Notification.builder()
                        .type(REPORT_ALARM_TYPE)
                        .subject(subject)
                        .content(htmlBuilder.buildReportAlarmHtml(saveCorpInfo, entry.getKey()))
                        .account(saveCorpInfo.getAccount())
                        .build();
                }))
            .collect(Collectors.toList());
    }

    private boolean isAlarmTimeMatching(PriceAlarm alarm, LocalTime now, int currentDayOfWeek) {
        LocalTime alarmTime = alarm.getTime();
        return alarmTime.getHour() == now.getHour() && 
               alarmTime.getMinute() == now.getMinute() &&
               alarm.fromWeekDayList().contains(currentDayOfWeek);
    }

    private List<Notification> createPriceAlarmNotifications(List<PriceAlarm> matchingAlarms, 
            Map<String, StockPriceInfoDTO> stockPriceInfoMap) {
        List<Notification> notifications = new ArrayList<>();

        for (PriceAlarm alarm : matchingAlarms) {
            SaveCorpInfo saveCorpInfo = alarm.getSaveCorpInfo();
            String stockCode = saveCorpInfo.getCorpInfo().getStockCode();
            StockPriceInfoDTO stockPriceInfo = stockPriceInfoMap.get(stockCode);

            if (stockPriceInfo == null) {
                log.warn("No stock price info found for code: {}", stockCode);
                continue;
            }

            String corpName = saveCorpInfo.getCorpInfo().getName();
            String subject = String.format("[%s] 현재가 %d원", corpName, stockPriceInfo.getClosingPrice());
            String htmlContent = htmlBuilder.buildPriceAlarmHtml(alarm, stockPriceInfo);

            notifications.add(Notification.builder()
                    .account(saveCorpInfo.getAccount())
                    .type(PRICE_ALARM_TYPE)
                    .subject(subject)
                    .content(htmlContent)
                    .build());
        }

        return notifications;
    }

    private List<Notification> processTargetPriceAlarms(Map<String, List<TargetPriceAlarm>> targetPriceAlarmMap,
            Map<String, StockPriceInfoDTO> stockPriceInfoMap) {
        List<Notification> notifications = new ArrayList<>();
        List<TargetPriceAlarm> triggeredAlarms = new ArrayList<>();

        for (Map.Entry<String, List<TargetPriceAlarm>> entry : targetPriceAlarmMap.entrySet()) {
            String stockCode = entry.getKey();
            StockPriceInfoDTO stockPriceInfo = stockPriceInfoMap.get(stockCode);

            if (stockPriceInfo == null) {
                log.warn("No stock price info found for code: {}", stockCode);
                continue;
            }

            for (TargetPriceAlarm alarm : entry.getValue()) {
                if (shouldTriggerAlarm(alarm, stockPriceInfo.getClosingPrice())) {
                    alarm.setAlarmed(true);
                    triggeredAlarms.add(alarm);

                    String corpName = alarm.getSaveCorpInfo().getCorpInfo().getName();
                    String subject = String.format("[%s] 목표가 도달! 현재가 %d원", corpName, stockPriceInfo.getClosingPrice());
                    String htmlContent = htmlBuilder.buildTargetPriceAlarmHtml(alarm, stockPriceInfo);

                    notifications.add(Notification.builder()
                            .account(alarm.getSaveCorpInfo().getAccount())
                            .type(TARGET_PRICE_ALARM_TYPE)
                            .subject(subject)
                            .content(htmlContent)
                            .build());
                }
            }
        }

        if (!triggeredAlarms.isEmpty()) {
            targetPriceAlarmRepository.saveAll(triggeredAlarms);
        }

        return notifications;
    }

    private boolean shouldTriggerAlarm(TargetPriceAlarm alarm, Long currentPrice) {
        return (alarm.isBuy() && currentPrice <= alarm.getTargetPrice()) ||
               (!alarm.isBuy() && currentPrice >= alarm.getTargetPrice());
    }
}
