package com.finance.adam.repository.pricealarm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class CreatePriceAlarmDTO {
    @NotNull(message = "기업정보를 선택해주세요.")
    private Long saveCorpInfoId;

    /**
     * 알람 설정 요일<br/>
     * 0 : 월요일, 1 : 화요일, 2 : 수요일, 3 : 목요일, 4 : 금요일, 5 : 토요일, 6 : 일요일
     */
    @NotNull(message = "요일을 선택해주세요.")
    private List<Integer> weekDayList;

    /**
     * 알람 설정 시간
     * ex) 09:00
     */
    @NotNull(message = "시간을 입력해주세요.")
    private LocalTime time;

    @NotNull(message = "알람 추가 정보를 선택해주세요.")
    private List<Integer> infoIndexList;
}
