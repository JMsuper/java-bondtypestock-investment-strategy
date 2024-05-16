package com.finance.adam.repository.pricealarm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePriceAlarmDTO {
    @NotNull
    private Long priceAlarmId;
    @NotNull
    private boolean active;
}
