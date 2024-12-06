package com.finance.adam.repository;

import com.finance.adam.repository.account.UserRepository;
import com.finance.adam.repository.account.domain.Account;
import com.finance.adam.repository.corpinfo.CorpRepository;
import com.finance.adam.repository.corpinfo.domain.CorpInfo;
import com.finance.adam.repository.reportalarm.ReportAlarmRepository;
import com.finance.adam.repository.reportalarm.domain.ReportAlarm;
import com.finance.adam.repository.reportalarm.domain.ReportType;
import com.finance.adam.repository.savecorpinfo.SaveCorpInfoRepository;
import com.finance.adam.repository.savecorpinfo.domain.SaveCorpInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ReportAlarmRepositoryTest {

    @Autowired
    ReportAlarmRepository reportAlarmRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CorpRepository corpRepository;
    @Autowired
    SaveCorpInfoRepository saveCorpInfoRepository;

    @Test
    void test_findAllByReportTypeAndCorpCodeList(){
        // given
        // 0. reportAlarm 삭제
        reportAlarmRepository.deleteAll();
        // 1. account 엔티티 생성 및 DB등록
        Account account = new Account("hello","hello@naver.com","123456","ROEL_USER");
        userRepository.save(account);
        // 2. corpinfo 엔티티 생성 및 DB등록
        CorpInfo corpInfo = CorpInfo.builder().corpCode("00363769").name("삼성전자").build();
        corpRepository.save(corpInfo);
        // 3. savecorpinfo 엔티티 생성 및 DB등록
        SaveCorpInfo saveCorpInfo = SaveCorpInfo.builder().account(account).corpInfo(corpInfo).build();
        saveCorpInfoRepository.save(saveCorpInfo);
        // 4. reportalarm 엔티티 생성 및 DB등록
        ReportAlarm reportAlarm = ReportAlarm.builder().saveCorpInfo(saveCorpInfo).reportType(ReportType.A).build();
        reportAlarmRepository.save(reportAlarm);
        ReportType reportType = ReportType.A;
        List<String> corpCodeList = List.of("00363769","00136341","00243757");

        // when
        List<ReportAlarm> reportAlarmList = reportAlarmRepository.findAllByReportTypeAndCorpCodeList(reportType,corpCodeList);

        // then
        assertThat(reportAlarmList).hasSize(1);
        assertThat(reportAlarmList.get(0).getSaveCorpInfo().getCorpInfo().getCorpCode()).isEqualTo("00363769");
        assertThat(reportAlarmList.get(0).getReportType()).isEqualTo(ReportType.A);
    }

   @Test
   void findAllByReportTypeAndCorpCodeList_일치하는데이터없음() {
       // given
       // 0. reportAlarm 삭제
       reportAlarmRepository.deleteAll();
       // 1. account 엔티티 생성 및 DB등록
       Account account = new Account("test", "test@test.com", "test1234", "ROLE_USER");
       userRepository.save(account);
       // 2. corpinfo 엔티티 생성 및 DB등록
       CorpInfo corpInfo = CorpInfo.builder().corpCode("00363769").name("삼성전자").build();
       corpRepository.save(corpInfo);
       // 3. savecorpinfo 엔티티 생성 및 DB등록
       SaveCorpInfo saveCorpInfo = SaveCorpInfo.builder().account(account).corpInfo(corpInfo).build();
       saveCorpInfoRepository.save(saveCorpInfo);
       // 4. reportalarm 엔티티 생성 및 DB등록
       ReportAlarm reportAlarm = ReportAlarm.builder()
               .saveCorpInfo(saveCorpInfo)
               .reportType(ReportType.A)
               .build();
       reportAlarmRepository.save(reportAlarm);

       // when
       List<ReportAlarm> reportAlarmList = reportAlarmRepository.findAllByReportTypeAndCorpCodeList(
               ReportType.B,  // 다른 ReportType으로 검색
               List.of("00363769")
       );

       // then
       assertThat(reportAlarmList).isEmpty();
   }

   @Test
   void findAllByReportTypeAndCorpCodeList_여러개의일치하는데이터() {
       // given
       // 0. reportAlarm 삭제
       reportAlarmRepository.deleteAll();
       // 1. account 엔티티 생성 및 DB등록
       Account account = new Account("test", "test@test.com", "test1234", "ROLE_USER");
       userRepository.save(account);

       // 2. 3개의 다른 기업 정보 생성 및 DB등록
       CorpInfo samsung = CorpInfo.builder().corpCode("00363769").name("삼성전자").build();
       CorpInfo lg = CorpInfo.builder().corpCode("00136341").name("LG전자").build();
       CorpInfo sk = CorpInfo.builder().corpCode("00243757").name("SK하이닉스").build();
       corpRepository.saveAll(List.of(samsung, lg, sk));

       // 3. savecorpinfo 엔티티들 생성 및 DB등록
       SaveCorpInfo saveCorpInfo1 = SaveCorpInfo.builder().account(account).corpInfo(samsung).build();
       SaveCorpInfo saveCorpInfo2 = SaveCorpInfo.builder().account(account).corpInfo(lg).build();
       SaveCorpInfo saveCorpInfo3 = SaveCorpInfo.builder().account(account).corpInfo(sk).build();
       saveCorpInfoRepository.saveAll(List.of(saveCorpInfo1, saveCorpInfo2, saveCorpInfo3));

       // 4. reportalarm 엔티티들 생성 및 DB등록
       ReportAlarm alarm1 = ReportAlarm.builder()
               .saveCorpInfo(saveCorpInfo1)
               .reportType(ReportType.A)
               .build();
       ReportAlarm alarm2 = ReportAlarm.builder()
               .saveCorpInfo(saveCorpInfo2)
               .reportType(ReportType.A)
               .build();
       ReportAlarm alarm3 = ReportAlarm.builder()
               .saveCorpInfo(saveCorpInfo3)
               .reportType(ReportType.A)
               .build();
       reportAlarmRepository.saveAll(List.of(alarm1, alarm2, alarm3));

       // when
       List<ReportAlarm> reportAlarmList = reportAlarmRepository.findAllByReportTypeAndCorpCodeList(
               ReportType.A,
               List.of("00363769", "00136341", "00243757")
       );

       // then
       assertThat(reportAlarmList).hasSize(3);
       assertThat(reportAlarmList).extracting(alarm -> alarm.getSaveCorpInfo().getCorpInfo().getCorpCode())
               .containsExactlyInAnyOrder("00363769", "00136341", "00243757");
       assertThat(reportAlarmList).extracting("reportType")
               .containsOnly(ReportType.A);
   }

   @Test
   void findAllByReportTypeAndCorpCodeList_다른ReportType데이터존재() {
       // given
       // 0. reportAlarm 삭제
       reportAlarmRepository.deleteAll();
       // 1. account 엔티티 생성 및 DB등록
       Account account = new Account("test", "test@test.com", "test1234", "ROLE_USER");
       userRepository.save(account);
       // 2. corpinfo 엔티티 생성 및 DB등록
       CorpInfo corpInfo = CorpInfo.builder().corpCode("00363769").name("삼성전자").build();
       corpRepository.save(corpInfo);
       // 3. savecorpinfo 엔티티 생성 및 DB등록
       SaveCorpInfo saveCorpInfo = SaveCorpInfo.builder().account(account).corpInfo(corpInfo).build();
       saveCorpInfoRepository.save(saveCorpInfo);

       // 4. 동일한 기업에 대해 다른 ReportType을 가진 알람들 생성
       ReportAlarm alarmTypeA = ReportAlarm.builder()
               .saveCorpInfo(saveCorpInfo)
               .reportType(ReportType.A)
               .build();
       ReportAlarm alarmTypeB = ReportAlarm.builder()
               .saveCorpInfo(saveCorpInfo)
               .reportType(ReportType.B)
               .build();
       reportAlarmRepository.saveAll(List.of(alarmTypeA, alarmTypeB));

       // when
       List<ReportAlarm> reportAlarmList = reportAlarmRepository.findAllByReportTypeAndCorpCodeList(
               ReportType.A,
               List.of("00363769")
       );

       // then
       assertThat(reportAlarmList).hasSize(1);
       assertThat(reportAlarmList.get(0).getSaveCorpInfo().getCorpInfo().getCorpCode()).isEqualTo("00363769");
       assertThat(reportAlarmList.get(0).getReportType()).isEqualTo(ReportType.A);
   }
}
