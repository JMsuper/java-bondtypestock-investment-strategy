package com.finance.adam.service;

import com.finance.adam.repository.account.UserRepository;
import com.finance.adam.repository.pricealarm.PriceAlarmRepository;
import com.finance.adam.repository.reportalarm.ReportAlarmRepository;
import com.finance.adam.repository.reportalarm.domain.ReportAlarm;
import com.finance.adam.repository.reportalarm.domain.ReportType;
import com.finance.adam.repository.savecorpinfo.SaveCorpInfoRepository;
import com.finance.adam.repository.savecorpinfo.domain.SaveCorpInfo;
import com.finance.adam.repository.targetpricealarm.TargetPriceAlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmCheckService {

    private final TargetPriceAlarmRepository targetPriceAlarmRepository;
    private final PriceAlarmRepository priceAlarmRepository;
    private final SaveCorpInfoRepository saveCorpInfoRepository;
    private final UserRepository userRepository;
    private final ReportAlarmRepository reportAlarmRepository;


    /*
        목적 : 공시 알람 트리거
        하는일 : 공시유형과 기업코드 리스트를 전달받아, 어떤 사용자에게 공시알람을 보내야되는지 지정
    */
    public HashMap<ReportType,List<SaveCorpInfo>> triggerReportAlarm(HashMap<ReportType,List<String>> reportTypeMap){
        // 0. 결과를 담을 맵 생성(key : 공시유형, value : 기업등록정보 리스트)
        HashMap<ReportType, List<SaveCorpInfo>> resultMap = new HashMap<>();

        // 1. key에 해당하는 공시유형 순회
        for(ReportType reportType : reportTypeMap.keySet()){
            // 2. 공시유형별 알람 대상 조회
            List<ReportAlarm> reportAlarmList =
                    reportAlarmRepository.findAllByReportTypeAndCorpCodeList(
                            reportType, reportTypeMap.get(reportType)
                    );
            resultMap.put(reportType, reportAlarmList.stream().map((ReportAlarm::getSaveCorpInfo)).toList());
        }

        // 3. 결과 맵 반환
        return resultMap;
    }



    // * 주가 정기 알람 트리거


    // * 목표가 알람 트리거
}
