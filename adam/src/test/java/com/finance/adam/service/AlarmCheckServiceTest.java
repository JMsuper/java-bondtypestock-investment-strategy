package com.finance.adam.service;

import com.finance.adam.repository.corpinfo.CorpRepository;
import com.finance.adam.repository.corpinfo.domain.CorpInfo;
import com.finance.adam.repository.reportalarm.ReportAlarmRepository;
import com.finance.adam.repository.reportalarm.domain.ReportAlarm;
import com.finance.adam.repository.reportalarm.domain.ReportType;
import com.finance.adam.repository.savecorpinfo.SaveCorpInfoRepository;
import com.finance.adam.repository.savecorpinfo.domain.SaveCorpInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
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

    @Test
    @DisplayName("공시 알람 트리거 테스트 - 정상 케이스")
    void triggerReportAlarm_Success() {
        // given
        reportAlarmRepository.deleteAll();
        HashMap<ReportType, List<String>> input = new HashMap<>();
        input.put(ReportType.B, Arrays.asList("005930", "035720"));
        input.put(ReportType.C, Arrays.asList("005930"));

        CorpInfo corpInfo1 = CorpInfo.builder().corpCode("005930").name("삼성전자").build();
        CorpInfo corpInfo2 = CorpInfo.builder().corpCode("035720").name("LG전자").build();
        corpRepository.saveAll(List.of(corpInfo1,corpInfo2));

        SaveCorpInfo saveCorpInfo1 = SaveCorpInfo.builder()
                .corpInfo(corpInfo1)
                .build();
        SaveCorpInfo saveCorpInfo2 = SaveCorpInfo.builder()
                .corpInfo(corpInfo2)
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
        HashMap<ReportType, List<SaveCorpInfo>> result = alarmCheckService.triggerReportAlarm(input);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(ReportType.B)).hasSize(2);
        assertThat(result.get(ReportType.C)).hasSize(1);

        assertTrue(result.get(ReportType.C).get(0).getCorpInfo().getCorpCode().equals(corpInfo1.getCorpCode()));
    }
    
}