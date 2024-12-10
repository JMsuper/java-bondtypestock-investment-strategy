package com.finance.adam.repository.pricealarm.dto;

import com.finance.adam.repository.corpinfo.domain.CorpInfo;
import com.finance.adam.repository.pricealarm.domain.PriceAlarm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PriceAlarmDTO {

    private Long id;

    private String stockName;

    private String stockCode;

    private List<Integer> weekDayList;

    private LocalTime time;

    private List<Integer> infoIndexList;

    private boolean active;

    public static PriceAlarmDTO from(PriceAlarm priceAlarm, CorpInfo corpInfo) {
        return PriceAlarmDTO.builder()
                .id(priceAlarm.getId())
                .stockName(corpInfo.getName())
                .stockCode(corpInfo.getStockCode())
                .weekDayList(priceAlarm.fromWeekDayList())
                .time(priceAlarm.getTime())
                .infoIndexList(priceAlarm.fromInfoIndexList())
                .active(priceAlarm.isActive())
                .build();
    }
}