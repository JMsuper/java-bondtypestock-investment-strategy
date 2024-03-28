package com.finance.adam.controller;

import com.finance.adam.dto.StepFiveStockPriceDTO;
import com.finance.adam.dto.StepFiveStockPriceRequestDTO;
import com.finance.adam.dto.StepOneStockInfoDTO;
import com.finance.adam.service.ScreeningService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/screening")
public class ScreeningController {

    private final ScreeningService screeningService;

    public ScreeningController(ScreeningService screeningService) {
        this.screeningService = screeningService;
    }

    @GetMapping("/step1")
    public List<StepOneStockInfoDTO> getStepOneStockInfoList(){
        return screeningService.getStepOneStockInfoList();
    }

    @PostMapping("/step5")
    public StepFiveStockPriceDTO getStockOpeningPrice(@RequestBody StepFiveStockPriceRequestDTO requestDTO){
        return screeningService.getStockOpeningPrice(requestDTO.getStockCodeList());
    }
}
