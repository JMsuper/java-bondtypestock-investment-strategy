package com.finance.adam.service;

import com.finance.adam.openapi.dart.dto.DartReportDTO;
import com.finance.adam.repository.account.UserRepository;
import com.finance.adam.repository.account.domain.Account;
import com.finance.adam.repository.corpinfo.CorpRepository;
import com.finance.adam.repository.corpinfo.domain.CorpInfo;
import com.finance.adam.repository.notification.domain.Notification;
import com.finance.adam.repository.reportalarm.ReportAlarmRepository;
import com.finance.adam.repository.reportalarm.domain.ReportAlarm;
import com.finance.adam.repository.reportalarm.domain.ReportType;
import com.finance.adam.repository.savecorpinfo.SaveCorpInfoRepository;
import com.finance.adam.repository.savecorpinfo.domain.SaveCorpInfo;
import com.finance.adam.repository.targetpricealarm.TargetPriceAlarmRepository;
import com.finance.adam.repository.pricealarm.PriceAlarmRepository;
import com.finance.adam.repository.pricealarm.domain.PriceAlarm;
import com.finance.adam.repository.stockprice.dto.StockPriceInfoDTO;
import com.finance.adam.repository.targetpricealarm.domain.TargetPriceAlarm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalTime;

@SpringBootTest
@ActiveProfiles("api")
@Transactional
class AlarmCheckServiceTest {

    @Autowired
    private AlarmCheckService alarmCheckService;
    @Autowired
    private ReportAlarmRepository reportAlarmRepository;
    @Autowired
    private SaveCorpInfoRepository saveCorpInfoRepository;
    @Autowired
    private CorpRepository corpRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private TargetPriceAlarmRepository targetPriceAlarmRepository;
    @Autowired
    private PriceAlarmRepository priceAlarmRepository;

    @Test
    @DisplayName("공시 알람 트리거 테스트 - 정상 케이스")
    void triggerReportAlarm_Success() {
        // given
        reportAlarmRepository.deleteAll();
        HashMap<ReportType, List<DartReportDTO>> input = new HashMap<>();
        input.put(ReportType.B, Arrays.asList(createDartReportDTO("005930"), createDartReportDTO("035720")));
        input.put(ReportType.C, Arrays.asList(createDartReportDTO("005930")));

        Account account = userRepository.findByEmail("tjsqls2067@naver.com").get(0);

        CorpInfo corpInfo1 = CorpInfo.builder().corpCode("005930").name("삼성전자").build();
        CorpInfo corpInfo2 = CorpInfo.builder().corpCode("035720").name("LG전자").build();
        corpRepository.saveAll(List.of(corpInfo1,corpInfo2));

        SaveCorpInfo saveCorpInfo1 = SaveCorpInfo.builder()
                .corpInfo(corpInfo1)
                .account(account)
                .build();
        SaveCorpInfo saveCorpInfo2 = SaveCorpInfo.builder()
                .corpInfo(corpInfo2)
                .account(account)
                .build();
        saveCorpInfoRepository.saveAll(List.of(saveCorpInfo1,saveCorpInfo2));

        ReportAlarm alarm1 = ReportAlarm.builder()
                .reportType(ReportType.B)
                .saveCorpInfo(saveCorpInfo1)
                .build();
        ReportAlarm alarm2 = ReportAlarm.builder()
                .reportType(ReportType.B)
                .saveCorpInfo(saveCorpInfo2)
                .build();
        ReportAlarm alarm3 = ReportAlarm.builder()
                .reportType(ReportType.C)
                .saveCorpInfo(saveCorpInfo1)
                .build();
        reportAlarmRepository.saveAll(List.of(alarm1,alarm2,alarm3));

        // when
        List<Notification> result = alarmCheckService.triggerReportAlarm(input);
        result.forEach(notificationService::handleNotification);
    }

    @Test
    @DisplayName("목표가 알림 트리거 테스트 - 정상 케이스")
    void triggerTargetPriceAlarm() {
        // given
        targetPriceAlarmRepository.deleteAll();
        Map<String, StockPriceInfoDTO> input = new HashMap<>();
        input.put("005930", StockPriceInfoDTO.builder().closingPrice(70000L)
                .build());
        input.put("035720", StockPriceInfoDTO.builder().closingPrice(30000L)
                .build());

        Account account = userRepository.findByEmail("tjsqls2067@naver.com").get(0);

        CorpInfo corpInfo1 = CorpInfo.builder().corpCode("005930").stockCode("005930").name("삼성전자").build();
        CorpInfo corpInfo2 = CorpInfo.builder().corpCode("035720").stockCode("035720").name("LG전자").build();
        corpRepository.saveAll(List.of(corpInfo1, corpInfo2));

        SaveCorpInfo saveCorpInfo1 = SaveCorpInfo.builder()
                .corpInfo(corpInfo1)
                .account(account)
                .build();
        SaveCorpInfo saveCorpInfo2 = SaveCorpInfo.builder()
                .corpInfo(corpInfo2)
                .account(account)
                .build();
        saveCorpInfoRepository.saveAll(List.of(saveCorpInfo1, saveCorpInfo2));

        TargetPriceAlarm alarm1 = TargetPriceAlarm.builder()
                .isBuy(false)
                .targetPrice(65000)
                .saveCorpInfo(saveCorpInfo1)
                .build();
        TargetPriceAlarm alarm2 = TargetPriceAlarm.builder()
                .isBuy(true)
                .targetPrice(75000)
                .saveCorpInfo(saveCorpInfo2)
                .build();
        targetPriceAlarmRepository.saveAll(List.of(alarm1, alarm2));

        // when
        List<Notification> result = alarmCheckService.triggerTargetPriceAlarm(input);

        // then
        assertThat(result).isNotEmpty(); // 결과가 비어있지 않은지 확인
        assertThat(result).hasSize(2); // 두 개의 알람이 트리거되어야 함
        result.forEach(notification -> {
            assertThat(notification.getContent()).isNotNull();
            notificationService.handleNotification(notification);
        });
    }

    @Test
    void triggerStockPriceAlarm() {
        // given
        priceAlarmRepository.deleteAll();
        Map<String, StockPriceInfoDTO> input = new HashMap<>();
        input.put("005930", StockPriceInfoDTO.builder()
                .stockCode("005930")
                .stockName("삼성전자")
                .closingPrice(70000L)
                .difference(1000L)
                .fluctuationRate(1.5D)
                .volume(1000000L)
                .build());

        Account account = userRepository.findByEmail("tjsqls2067@naver.com").get(0);

        CorpInfo corpInfo = CorpInfo.builder()
                .corpCode("005930")
                .stockCode("005930")
                .name("삼성전자")
                .build();
        corpRepository.save(corpInfo);

        SaveCorpInfo saveCorpInfo = SaveCorpInfo.builder()
                .corpInfo(corpInfo)
                .account(account)
                .build();
        saveCorpInfoRepository.save(saveCorpInfo);

        // 현재 시간 설정
        LocalTime currentTime = LocalTime.of(9, 0); // 오전 9시로 설정
        int currentDayOfWeek = 1; // 월요일로 설정

        PriceAlarm alarm = PriceAlarm.builder()
                .saveCorpInfo(saveCorpInfo)
                .time(currentTime)
                .weekDayList("[" + currentDayOfWeek + "]")
                .build();
        priceAlarmRepository.save(alarm);

        // when
        List<Notification> result = alarmCheckService.triggerStockPriceAlarm(input, currentTime, currentDayOfWeek);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        result.forEach(notification -> {
            assertThat(notification.getType()).isEqualTo("pricealarm");
            assertThat(notification.getContent()).isNotNull();
            assertThat(notification.getAccount()).isEqualTo(account);
            notificationService.handleNotification(notification);
        });
    }



    private DartReportDTO createDartReportDTO(String corpCd){
        DartReportDTO dto = new DartReportDTO();
        dto.setCorpName(corpCd);
        dto.setCorpCode(corpCd);
        return dto;
    }
    
}