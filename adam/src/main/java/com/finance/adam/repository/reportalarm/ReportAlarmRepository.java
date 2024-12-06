package com.finance.adam.repository.reportalarm;

import com.finance.adam.repository.reportalarm.domain.ReportAlarm;
import com.finance.adam.repository.reportalarm.domain.ReportType;
import com.finance.adam.repository.savecorpinfo.domain.SaveCorpInfo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReportAlarmRepository extends JpaRepository<ReportAlarm, Long> {

    // report_alarm [join] save_corp_info [join] corp_info
    @Query("""
        SELECT ra
        FROM ReportAlarm ra
        JOIN FETCH ra.saveCorpInfo sci
        JOIN FETCH sci.corpInfo cp
        WHERE ra.reportType = :reportType AND cp.corpCode IN :corpCodeList
    """)
    List<ReportAlarm> findAllByReportTypeAndCorpCodeList(ReportType reportType, List<String> corpCodeList);

    List<ReportAlarm> findAllBySaveCorpInfo(SaveCorpInfo saveCorpInfoId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("delete from ReportAlarm ra where ra.saveCorpInfo = :saveCorpInfoId")
    void deleteAllBySaveCorpInfo(SaveCorpInfo saveCorpInfoId);
}
