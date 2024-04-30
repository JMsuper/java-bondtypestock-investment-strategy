package com.finance.adam.controller;

import com.finance.adam.dto.StepOneStockInfoDTO;
import com.finance.adam.service.ScreeningService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
