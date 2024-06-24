package com.finance.adam.controller;

import com.finance.adam.dto.StepOneStockInfoDTO;
import com.finance.adam.openapi.dart.vo.OpenDartReportExtractedDTO;
import com.finance.adam.repository.financeinfo.dto.FinanceInfoDTO;
import com.finance.adam.dto.KrxCorpListResponse;
import com.finance.adam.service.FinanceDataService;
import com.finance.adam.service.ScreeningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/finances")
public class FinanceDataController {

    private FinanceDataService financeDataService;
    private ScreeningService screeningService;

    @GetMapping("/screening")
    public List<StepOneStockInfoDTO> getStepOneStockInfoList(){
        return screeningService.getStepOneStockInfoList();
    }

    @GetMapping("/stocks")
    public List<KrxCorpListResponse> getCorpInfoList(){
        List<KrxCorpListResponse> result = financeDataService.getKrxCorpInfo();
        return result;
    }

    @GetMapping("/{corpCode}")
    public List<FinanceInfoDTO> getFinanceInfos(@PathVariable String corpCode, @RequestParam int startYear, @RequestParam int endYear){
        List<FinanceInfoDTO> result = financeDataService.getFinanceInfos(corpCode, startYear, endYear);
        return result;
    }

    @GetMapping("/reports/{corpCode}")
    public List<OpenDartReportExtractedDTO> getReports(@PathVariable String corpCode){
        List<OpenDartReportExtractedDTO> result = financeDataService.getReports(corpCode);
        return result;
    }
}
