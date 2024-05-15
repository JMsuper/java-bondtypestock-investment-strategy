package com.finance.adam.repository.targetpricealarm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DeleteTargetPriceAlarmDTO {
    @NotNull
    private Long targetPriceAlarmId;
}
