package com.finance.adam.repository.pricealarm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DeletePriceAlarmDTO {
    @NotNull
    private Long priceAlarmId;
}
