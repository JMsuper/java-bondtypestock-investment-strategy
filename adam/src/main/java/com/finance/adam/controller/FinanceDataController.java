package com.finance.adam.controller;

import com.finance.adam.dto.StepOneStockInfoDTO;
import com.finance.adam.openapi.dart.dto.OpenDartReportExtractedDTO;
import com.finance.adam.repository.financeinfo.dto.FinanceInfoDTO;
import com.finance.adam.dto.KrxCorpListResponse;
import com.finance.adam.service.FinanceDataService;
import com.finance.adam.service.ScreeningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/finances")
public class FinanceDataController {

    private FinanceDataService financeDataService;
    private ScreeningService screeningService;

    public FinanceDataController(FinanceDataService financeDataService, ScreeningService screeningService){
        this.financeDataService = financeDataService;
        this.screeningService = screeningService;
    }

    @GetMapping("/screening")
    public List<StepOneStockInfoDTO> getStepOneStockInfoList(){
        log.info("Getting step one stock info list");
        List<StepOneStockInfoDTO> result = screeningService.getStepOneStockInfoList();
        log.debug("Retrieved {} stocks in step one screening", result.size());
        return result;
    }

    @GetMapping("/stocks")
    public List<KrxCorpListResponse> getCorpInfoList(){
        log.info("Getting KRX corporation list");
        List<KrxCorpListResponse> result = financeDataService.getKrxCorpInfo();
        log.debug("Retrieved {} corporations from KRX", result.size());
        return result;
    }

    @GetMapping("/{corpCode}")
    public List<FinanceInfoDTO> getFinanceInfos(@PathVariable String corpCode, @RequestParam int startYear, @RequestParam int endYear){
        log.info("Getting finance info for corporation: {} from year {} to {}", corpCode, startYear, endYear);
        List<FinanceInfoDTO> result = financeDataService.getFinanceInfos(corpCode, startYear, endYear);
        log.debug("Retrieved {} finance records for corporation {}", result.size(), corpCode);
        return result;
    }

    @GetMapping("/{corpCode}/reports")
    public List<OpenDartReportExtractedDTO> getReports(@PathVariable String corpCode){
        log.info("Getting reports for corporation: {}", corpCode);
        List<OpenDartReportExtractedDTO> result = financeDataService.getReports(corpCode);
        log.debug("Retrieved {} reports for corporation {}", result.size(), corpCode);
        return result;
    }
}
