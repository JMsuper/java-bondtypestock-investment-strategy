package com.finance.adam.controller;

import com.finance.adam.dto.KrxCorpListResponse;
import com.finance.adam.service.FinanceDataService;
import org.springframework.web.bind.annotation.GetMapping;
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
}
