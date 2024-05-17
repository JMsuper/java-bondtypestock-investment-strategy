package com.finance.adam.repository.reportalarm.dto;

import com.finance.adam.repository.reportalarm.domain.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Setter
@Getter
@Builder
public class ReportAlarmListDTO {
    private String stockName;
    private Long saveCorpInfoId;
    private List<ReportType> reportTypeList;
    private boolean active;
}
