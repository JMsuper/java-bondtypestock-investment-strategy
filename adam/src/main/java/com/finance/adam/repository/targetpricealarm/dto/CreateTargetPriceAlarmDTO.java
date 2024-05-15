package com.finance.adam.repository.targetpricealarm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateTargetPriceAlarmDTO {
    @NotNull
    private Long saveCorpInfoId;
    @NotBlank
    private String buyOrSell;
    @NotNull
    private int targetPrice;
    @NotNull
    private List<Integer> infoIndexList;
}
