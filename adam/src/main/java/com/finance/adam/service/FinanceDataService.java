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

    /**
     * - 연결 재무제표일 경우, CFS 라는 key 를 갖음(value는 0L) <br>
     * - 재무제표일 경우, OFS 라는 key 를 갖음(value는 0L)
     */
    public Map<String, Long> getFinancialInfo(String corpCode, String bsnsYear){
        List<OpenDartFinancialInfo> corpFinancialInfoList = openDartAPI.getCorpFinancialInfo(corpCode, bsnsYear);
        if(corpFinancialInfoList == null){
            return null;
        }

        // 연결 재무제표
        Map<String,Long> CFS = new HashMap<>();
        CFS.put("CFS",0L);

        // 재무제표
        Map<String,Long> OFS = new HashMap<>();
        OFS.put("OFS",0L);

        for(int i = 0; i < corpFinancialInfoList.size(); i++){
            OpenDartFinancialInfo info = corpFinancialInfoList.get(i);
            Map<String, Long> currentFs;
            if(info.getFsNm().equals("연결재무제표")){
                currentFs = CFS;
            }else{
                currentFs = OFS;
            }

            String name = info.getAccountNm();

            try{
                Long amount = Long.parseLong(info.getThstrmAmount().replaceAll(",",""));
                currentFs.put(name, amount);
            }catch (NumberFormatException e){
                log.info(e.getMessage());
                log.info("corpCode : " + corpCode + ", bsnsYear : " + bsnsYear + ", AccountNm : " + name);
            }

        }

        if(CFS.size() == 1){
            return OFS;
        }
        return CFS;
    }

    public void renewFinancialInfo(){
        List<CorpInfo> corpInfos = corpRepository.findAll();
        for(int i = 0; i < corpInfos.size(); i++){
            CorpInfo corpInfo = corpInfos.get(i);
            String corpCode = corpInfo.getCorpCode();

            if(corpCode.equals("00137809")){
                System.out.println();
            }

            for(int j = 0; j < 2; j++){
                int year = 2020 - j;
                Map<String, Long> info = getFinancialInfo(corpCode,String.valueOf(year));

                if(info == null){
                    continue;
                }


                FinanceInfo financeInfo = FinanceInfo.fromMap(info);
                if(info.containsKey("CFS")){
                    financeInfo.setFsDiv("연결재무제표");
                }else{
                    financeInfo.setFsDiv("재무제표");
                }
                financeInfo.setYear(year);
                financeInfo.setCorpInfo(corpInfo);
                financeInfoRepository.save(financeInfo);
            }
        }
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
