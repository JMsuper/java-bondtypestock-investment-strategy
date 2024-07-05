package com.finance.adam.openapi.dart.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OpenDartReportExtractedDTO {
    private String corpName;

    private String reportNm;

    private String rceptNo;

    private String flrNm;

    private String rceptDt;

    private String rm;

    public static OpenDartReportExtractedDTO from(DartReportDTO dto) {
        return OpenDartReportExtractedDTO.builder()
                .corpName(dto.getCorpName())
                .reportNm(dto.getReportNm())
                .rceptNo(dto.getRceptNo())
                .flrNm(dto.getFlrNm())
                .rceptDt(dto.getRceptDt())
                .rm(dto.getRm())
                .build();
    }
}
