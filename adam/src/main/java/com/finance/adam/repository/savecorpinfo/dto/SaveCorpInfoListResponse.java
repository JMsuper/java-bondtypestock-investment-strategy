package com.finance.adam.repository.savecorpinfo.dto;

import com.finance.adam.repository.memo.dto.MemoDTO;
import com.finance.adam.repository.stockprice.dto.StockPriceInfoResponseDTO;
import com.finance.adam.openapi.dart.dto.OpenDartReportExtractedDTO;
import com.finance.adam.repository.corpinfo.domain.CorpInfo;
import com.finance.adam.repository.savecorpinfo.domain.SaveCorpInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SaveCorpInfoListResponse {
    private Long saveCorpInfoId;

    private LocalDateTime searchTime;

    private String stockCd;

    private String name;

    private String corpCd;

    private String market;

    private Long bps;

    private Float targetRate;

    private Integer targetPrice;

    private Float expectedRate;

    private Float afterTenYearsAverageROE;

    private StockPriceInfoResponseDTO stockPriceInfo;

    private List<OpenDartReportExtractedDTO> reportList;

    private List<MemoDTO> memoList;

    public static SaveCorpInfoListResponse fromSaveCorpInfo(
            SaveCorpInfo saveCorpInfo,
            List<OpenDartReportExtractedDTO> reportList
            ){

        CorpInfo corpInfo = saveCorpInfo.getCorpInfo();

        // targetRate를 소수점 2자리까지만 표시
        float parsedTargetRate = (float) ((int)(saveCorpInfo.getTargetRate() * 10000)) / 100;

        return SaveCorpInfoListResponse.builder()
                .saveCorpInfoId(saveCorpInfo.getId())
                .searchTime(corpInfo.getStockPrice().getUpdatedAt())
                .corpCd(corpInfo.getCorpCode())
                .stockCd(corpInfo.getStockCode())
                .name(corpInfo.getName())
                .afterTenYearsAverageROE(saveCorpInfo.getAfterTenYearsAverageROE())
                .market(corpInfo.getMarket())
                .targetRate(parsedTargetRate)
                .stockPriceInfo(StockPriceInfoResponseDTO.fromStockPrice(corpInfo.getStockPrice()))
                .reportList(reportList)
                .memoList(saveCorpInfo.getMemoList().stream().map(MemoDTO::from).toList())
                .build();
    }

    public static SaveCorpInfoListResponse fromSaveCorpInfo(
            SaveCorpInfo saveCorpInfo,
            List<OpenDartReportExtractedDTO> reportList,
            long bps
    ){
        SaveCorpInfoListResponse result = fromSaveCorpInfo(saveCorpInfo, reportList);
        result.setBps(bps);
        return result;
    }

    public static SaveCorpInfoListResponse fromSaveCorpInfo(
            SaveCorpInfo saveCorpInfo,

            List<OpenDartReportExtractedDTO> reportList,
            long bps,
            int targetPrice,
            float expectedRate
            ){

        SaveCorpInfoListResponse result = fromSaveCorpInfo(saveCorpInfo, reportList,bps);

        // expectedRate를 소수점 2자리까지만 표시
        float parsedExpectedRate = (float) ((int) (expectedRate * 10000)) / 100;

        result.setExpectedRate(parsedExpectedRate);
        result.setTargetPrice(targetPrice);

        return result;
    }
}
