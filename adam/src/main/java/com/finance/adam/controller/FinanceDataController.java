package com.finance.adam.controller;

import com.finance.adam.dto.FinanceInfoDTO;
import com.finance.adam.dto.KrxCorpListResponse;
import com.finance.adam.repository.domain.FinanceInfo;
import com.finance.adam.service.FinanceDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class FinanceDataController {

    private FinanceDataService financeDataService;

    public FinanceDataController(FinanceDataService financeDataService){
        this.financeDataService = financeDataService;
    }

    @GetMapping("/stockInfos")
    public List<KrxCorpListResponse> getCorpInfoList(){
        List<KrxCorpListResponse> result = financeDataService.getKrxCorpInfo();
        return result;
    }

    @GetMapping("/financeInfo/{corpCode}")
    public List<FinanceInfoDTO> getFinanceInfos(@PathVariable String corpCode, @RequestParam int startYear, @RequestParam int endYear){
        List<FinanceInfoDTO> result = financeDataService.getFinanceInfos(corpCode, startYear, endYear);
        return result;
    }
}
