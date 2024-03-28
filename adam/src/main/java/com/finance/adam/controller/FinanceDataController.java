package com.finance.adam.controller;

import com.finance.adam.dto.FinanceInfoDTO;
import com.finance.adam.dto.KrxCorpListResponse;
import com.finance.adam.repository.domain.FinanceInfo;
import com.finance.adam.service.FinanceDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FinanceDataController {

    private FinanceDataService financeDataService;

    public FinanceDataController(FinanceDataService financeDataService){
        this.financeDataService = financeDataService;
    }

    @GetMapping("/stockInfos")
    public List<KrxCorpListResponse> getCorpInfoList(){
        return financeDataService.getKrxCorpInfo();
    }

    @GetMapping("/financeInfo/{stockCode}")
    public List<FinanceInfoDTO> getFinanceInfos(@PathVariable String stockCode, @RequestParam int startYear, @RequestParam int endYear){
        List<FinanceInfoDTO> result = financeDataService.getFinanceInfos(stockCode, startYear, endYear);
        System.out.println(stockCode + " " + startYear + " " + endYear);
        return result;
    }
}
