package com.finance.adam.repository.targetpricealarm.dto;


import com.finance.adam.repository.corpinfo.domain.CorpInfo;
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

    private String stockName;

    private String stockCode;

    private String buyOrSell;

    private int targetPrice;

    private List<Integer> infoIndexList;

    private boolean active;

    private boolean alarmed;

    public static TargetPriceAlarmDTO from(TargetPriceAlarm targetPriceAlarm, CorpInfo corpInfo) {
        return TargetPriceAlarmDTO.builder()
                .id(targetPriceAlarm.getId())
                .stockName(corpInfo.getName())
                .stockCode(corpInfo.getStockCode())
                .buyOrSell(targetPriceAlarm.isBuy() ? "매수" : "매도")
                .targetPrice(targetPriceAlarm.getTargetPrice())
                .infoIndexList(targetPriceAlarm.fromInfoIndexList())
                .active(targetPriceAlarm.isActive())
                .alarmed(targetPriceAlarm.isAlarmed())
                .build();
    }
}
