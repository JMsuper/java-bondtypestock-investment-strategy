package com.finance.adam.repository.reportalarm.dto;

import com.finance.adam.repository.reportalarm.domain.ReportType;
import com.finance.adam.validation.ValidReportType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
;

import java.util.List;

public class UpdateReportAlarmDTO {

    @Getter
    @NotNull
    private Long saveCorpInfoId;

    @NotNull
    @Valid
    private List<ReportTypeWrapper> reportTypeList;

    @Getter
    @NotNull
    private Boolean active;

    public List<ReportType> getReportTypeList() {
        return reportTypeList.stream()
                .map(ReportTypeWrapper::getReportType)
                .toList();
    }

    @Data
    @NoArgsConstructor
    public static class ReportTypeWrapper{

        @ValidReportType(enumClass = ReportType.class)
        private ReportType reportType;

        public ReportTypeWrapper(String reportType){
            this.reportType = ReportType.valueOf(reportType);
        }
    }
}
