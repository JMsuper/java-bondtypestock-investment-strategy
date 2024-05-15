package com.finance.adam.repository.targetpricealarm.dto;


import com.finance.adam.repository.targetpricealarm.domain.TargetPriceAlarm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TargetPriceAlarmDTO {

    private Long id;

    private String buyOrSell;

    private int targetPrice;

    private List<Integer> infoIndexList;

    private boolean active;

    private boolean alarmed;

    public static TargetPriceAlarmDTO from(TargetPriceAlarm targetPriceAlarm) {
        return TargetPriceAlarmDTO.builder()
                .id(targetPriceAlarm.getId())
                .buyOrSell(targetPriceAlarm.isBuy() ? "매수" : "매도")
                .targetPrice(targetPriceAlarm.getTargetPrice())
                .infoIndexList(targetPriceAlarm.fromInfoIndexList())
                .active(targetPriceAlarm.isActive())
                .alarmed(targetPriceAlarm.isAlarmed())
                .build();
    }
}
