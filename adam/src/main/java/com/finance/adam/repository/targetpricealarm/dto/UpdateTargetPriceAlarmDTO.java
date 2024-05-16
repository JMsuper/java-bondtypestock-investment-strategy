package com.finance.adam.repository.targetpricealarm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTargetPriceAlarmDTO {
    @NotNull
    private Long targetPriceAlarmId;
    @NotNull
    private boolean active;
}
