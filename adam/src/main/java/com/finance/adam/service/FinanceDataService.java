package com.finance.adam.service;

import com.finance.adam.dto.KrxCorpListResponse;
import com.finance.adam.openapi.dart.OpenDartAPI;
import com.finance.adam.openapi.dart.vo.OpenDartFinancialInfo;
import com.finance.adam.openapi.publicdataportal.PublicDataPortalOpenAPI;
import com.finance.adam.openapi.publicdataportal.vo.KrxItemInfo;
import com.finance.adam.repository.CorpRepository;
import com.finance.adam.repository.FinanceInfoRepository;
import com.finance.adam.repository.domain.CorpInfo;
import com.finance.adam.repository.domain.FinanceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FinanceDataService {

    private OpenDartAPI openDartAPI;
    private PublicDataPortalOpenAPI publicDataPortalOpenAPI;
    private CorpRepository corpRepository;
    private FinanceInfoRepository financeInfoRepository;

    public FinanceDataService(OpenDartAPI openDartAPI,
                              PublicDataPortalOpenAPI publicDataPortalOpenAPI,
                              CorpRepository corpRepository,
                              FinanceInfoRepository financeInfoRepository){
        this.openDartAPI = openDartAPI;
        this.publicDataPortalOpenAPI = publicDataPortalOpenAPI;
        this.corpRepository = corpRepository;
        this.financeInfoRepository = financeInfoRepository;
    }

    public List<KrxCorpListResponse> getKrxCorpInfo(){
        List<CorpInfo> corpInfos = corpRepository.findAll();
        return corpInfos.stream()
                .map((corpInfo -> KrxCorpListResponse.fromCorpInfo(corpInfo)))
                .collect(Collectors.toList());
    }

    public Map<String, Long> getFinancialInfo(String corpCode, String bsnsYear){
        List<OpenDartFinancialInfo> corpFinancialInfoList = openDartAPI.getCorpFinancialInfo(corpCode, bsnsYear);
        Map<String,Long> accountInfo = new HashMap<>();

        for(int i = 0; i < corpFinancialInfoList.size(); i++){
            OpenDartFinancialInfo info = corpFinancialInfoList.get(i);
            if(!info.getFsNm().equals("연결재무제표")){
                continue;
            }

            String name = info.getAccountNm();
            Long amount = Long.parseLong(info.getThstrmAmount().replaceAll(",",""));
            accountInfo.put(name, amount);
        }

        return accountInfo;
    }

    @Transactional
    public void renewFinancialInfo(){
        List<CorpInfo> corpInfos = corpRepository.findAll();
//        for(int i = 0; i < corpInfos.size(); i++){
            CorpInfo corpInfo = corpInfos.get(0);
            String corpCode = corpInfo.getCorpCode();

            for(int j = 0; j < 3; j++){
                int year = 2023 - j;
                Map<String, Long> info = getFinancialInfo(corpCode,String.valueOf(year));
                FinanceInfo financeInfo = FinanceInfo.fromMap(info);
                financeInfoRepository.save(financeInfo);
            }
//        }
    }

    @Transactional
    public void renewKrxStockList(){
        List<KrxItemInfo> krxItemInfos = publicDataPortalOpenAPI.getKrxItemInfoList();
        Map<String,String> corpCodeMap = openDartAPI.getCorpCodeMap();

        for(KrxItemInfo info : krxItemInfos){
            CorpInfo corpInfo = CorpInfo.fromKrxItemInfo(info);
            String corpCode = corpCodeMap.get(corpInfo.getParsedStockCode());

            if(corpCode == null || corpCode.isEmpty()){
                log.warn("종목코드와 매핑된 공시 기업코드가 없습니다. Open Dart API를 확인하세요.");
            }else {
                corpInfo.setCorpCode(corpCode);
            }
            corpRepository.save(corpInfo);
        }
    }
}
