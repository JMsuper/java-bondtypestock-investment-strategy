package com.finance.adam.service;

import com.finance.adam.openapi.dart.OpenDartAPI;
import com.finance.adam.openapi.publicdataportal.PublicDataPortalOpenAPI;
import com.finance.adam.openapi.publicdataportal.vo.KrxItemInfo;
import com.finance.adam.repository.CorpRepository;
import com.finance.adam.repository.domain.CorpInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FinanceDataService {

    private OpenDartAPI openDartAPI;
    private PublicDataPortalOpenAPI publicDataPortalOpenAPI;
    private CorpRepository corpRepository;

    public FinanceDataService(OpenDartAPI openDartAPI, PublicDataPortalOpenAPI publicDataPortalOpenAPI, CorpRepository corpRepository){
        this.openDartAPI = openDartAPI;
        this.publicDataPortalOpenAPI = publicDataPortalOpenAPI;
        this.corpRepository = corpRepository;
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
