package com.finance.adam.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class StepFiveStockPriceDTO {
    private LocalDateTime searchTime;
    private Map<String, Long> openingPriceMap;

}
