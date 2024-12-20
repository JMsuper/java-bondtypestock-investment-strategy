package com.finance.adam.util;

import com.finance.adam.openapi.dart.dto.DartReportDTO;
import com.finance.adam.repository.pricealarm.domain.PriceAlarm;
import com.finance.adam.repository.savecorpinfo.domain.SaveCorpInfo;
import com.finance.adam.repository.savecorpinfo.dto.SaveCorpInfoListResponse;
import com.finance.adam.repository.stockprice.dto.StockPriceInfoDTO;
import com.finance.adam.repository.targetpricealarm.domain.TargetPriceAlarm;
import com.finance.adam.service.CorpInfoService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class HtmlBuilder {

    private static final String FOOTER_HTML = """
        <div style='margin-top: 16px; padding-top: 12px; border-top: 1px solid #e0e0e0; text-align: center; font-size: 0.875rem; color: #757575;'>
            <p>자세한 정보는 <a href='https://snowball-stock.vercel.app/' target='_blank' style='color: #1976d2; text-decoration: none;'>Snowball Stock</a>에서 확인하세요.</p>
        </div>
    """;

    private final CorpInfoService corpInfoService;

    public HtmlBuilder(CorpInfoService corpInfoService) {
        this.corpInfoService = corpInfoService;
    }

    public String buildReportAlarmHtml(SaveCorpInfo saveCorpInfo, DartReportDTO dartReportDTO) {
        StringBuilder html = new StringBuilder();
        html.append("<div style='padding: 16px; background-color: #ffffff; border: 1px solid #e0e0e0; border-radius: 8px; font-family: Arial, sans-serif;'>");

        html.append("<h3 style='color: #1976d2; margin: 0; font-size: 1.5rem;'>")
                .append(saveCorpInfo.getCorpInfo().getName())
                .append("</h3>");

        html.append("<p style='color: #757575; margin-top: 8px; font-size: 1rem;'>새로운 공시가 등록되었습니다:</p>");

        html.append("<div style='padding: 12px; background-color: #f5f5f5; border-radius: 4px;'>");
        html.append("<ul style='list-style-type: none; padding: 0; margin: 0;'>");
        html.append("<li style='margin-bottom: 8px;'><strong>법인구분:</strong> ").append(dartReportDTO.getCorpCls()).append("</li>");
        html.append("<li style='margin-bottom: 8px;'><strong>종목명:</strong> ").append(dartReportDTO.getCorpName()).append("</li>");
        html.append("<li style='margin-bottom: 8px;'><strong>종목코드:</strong> ").append(dartReportDTO.getCorpCode()).append("</li>");
        html.append("<li style='margin-bottom: 8px;'><strong>주식코드:</strong> ").append(dartReportDTO.getStockCode()).append("</li>");
        html.append("<li style='margin-bottom: 8px;'><strong>보고서명:</strong> ").append(dartReportDTO.getReportNm()).append("</li>");
        html.append("<li style='margin-bottom: 8px;'><strong>접수번호:</strong> ").append(dartReportDTO.getRceptNo()).append("</li>");
        html.append("<li style='margin-bottom: 8px;'><strong>공시 제출인:</strong> ").append(dartReportDTO.getFlrNm()).append("</li>");
        html.append("<li style='margin-bottom: 8px;'><strong>접수일자:</strong> ").append(dartReportDTO.getRceptDt()).append("</li>");
        html.append("<li style='margin-bottom: 8px;'><strong>비고:</strong> ").append(dartReportDTO.getRm()).append("</li>");
        html.append("</ul>");
        html.append("</div>");
        
        html.append("<p style='margin-top: 16px; font-size: 1rem;'>자세한 내용은 <a href='https://dart.fss.or.kr/dsaf001/main.do?rcpNo=")
                .append(dartReportDTO.getRceptNo())
                .append("' target='_blank' style='color: #1976d2; text-decoration: none;'>DART</a>에서 확인하실 수 있습니다.</p>");

        html.append(FOOTER_HTML);
        html.append("</div>");
        return html.toString();
    }

    public String buildPriceAlarmHtml(PriceAlarm alarm, StockPriceInfoDTO stockPriceInfo) {
        StringBuilder html = new StringBuilder();
        html.append("<div style='padding: 16px; background-color: #ffffff; border: 1px solid #e0e0e0; border-radius: 8px; font-family: Arial, sans-serif;'>");

        html.append("<h3 style='color: #1976d2; margin: 0; font-size: 1.5rem;'>")
                .append(stockPriceInfo.getStockName())
                .append(" 주가 알림</h3>");

        html.append("<p style='color: #757575; margin-top: 8px; font-size: 1rem;'>정기 주가 알림입니다.</p>");

        html.append("<div style='padding: 12px; background-color: #f5f5f5; border-radius: 4px;'>");
        html.append("<ul style='list-style-type: none; padding: 0; margin: 0;'>");
        html.append("<li style='margin-bottom: 8px;'><strong>종목코드:</strong> ").append(stockPriceInfo.getStockCode()).append("</li>");
        html.append("<li style='margin-bottom: 8px;'><strong>종목명:</strong> ").append(stockPriceInfo.getStockName()).append("</li>");
        html.append("<li style='margin-bottom: 8px;'><strong>시장구분:</strong> ").append(stockPriceInfo.getMarketType()).append("</li>");
        html.append("<li style='margin-bottom: 8px;'><strong>업종:</strong> ").append(stockPriceInfo.getDepartment()).append("</li>");
        html.append("<li style='margin-bottom: 8px;'><strong>종가:</strong> ").append(String.format("%,d", stockPriceInfo.getClosingPrice())).append("원</li>");
        html.append("<li style='margin-bottom: 8px;'><strong>전일대비:</strong> ").append(String.format("%,d", stockPriceInfo.getDifference())).append("원 (")
                .append(String.format("%.2f", stockPriceInfo.getFluctuationRate())).append("%)</li>");
        html.append("<li style='margin-bottom: 8px;'><strong>거래량:</strong> ").append(String.format("%,d", stockPriceInfo.getVolume())).append("주</li>");
        html.append("</ul>");
        html.append("</div>");

        html.append(createRegisteredAlarmInfos(alarm));

        html.append(FOOTER_HTML);
        html.append("</div>");
        return html.toString();
    }

    public String buildTargetPriceAlarmHtml(TargetPriceAlarm alarm, StockPriceInfoDTO stockPriceInfo) {
        StringBuilder html = new StringBuilder();
        html.append("<div style='padding: 16px; background-color: #ffffff; border: 1px solid #e0e0e0; border-radius: 8px; font-family: Arial, sans-serif;'>");

        html.append("<h3 style='color: #1976d2; margin: 0; font-size: 1.5rem;'>")
                .append(stockPriceInfo.getStockName())
                .append(" 목표가 ��</h3>");

        html.append("<p style='color: #757575; margin-top: 8px; font-size: 1rem;'>설정한 ")
                .append(alarm.isBuy() ? "매수" : "매도")
                .append(" 목표가에 도달했습니다.</p>");

        html.append("<div style='padding: 12px; background-color: #f5f5f5; border-radius: 4px;'>");
        html.append("<ul style='list-style-type: none; padding: 0; margin: 0;'>");
        html.append("<li style='margin-bottom: 8px;'><strong>목표가:</strong> ").append(String.format("%,d", alarm.getTargetPrice())).append("원</li>");
        html.append("<li style='margin-bottom: 8px;'><strong>현재가:</strong> ").append(String.format("%,d", stockPriceInfo.getClosingPrice())).append("원</li>");
        html.append("<li style='margin-bottom: 8px;'><strong>전일대비:</strong> ").append(String.format("%,d", stockPriceInfo.getDifference())).append("원 (")
                .append(String.format("%.2f", stockPriceInfo.getFluctuationRate())).append("%)</li>");
        html.append("</ul>");
        html.append("</div>");

        html.append(createRegisteredAlarmInfos(alarm));

        html.append(FOOTER_HTML);
        html.append("</div>");
        return html.toString();
    }

    
     public String createRegisteredAlarmInfos(PriceAlarm priceAlarm) {
        SaveCorpInfoListResponse response = corpInfoService.calculateSaveCorpInfoResponse(priceAlarm.getSaveCorpInfo());
        List<AlarmAddedInfo> registeredInfos = getRegisteredAlarmInfos(priceAlarm);
        return buildAdditionalInfoHtml(registeredInfos, response);
    }

    public String createRegisteredAlarmInfos(TargetPriceAlarm targetPriceAlarm) {
        SaveCorpInfoListResponse response = corpInfoService.calculateSaveCorpInfoResponse(targetPriceAlarm.getSaveCorpInfo());
        List<AlarmAddedInfo> registeredInfos = getRegisteredAlarmInfos(targetPriceAlarm);
        return buildAdditionalInfoHtml(registeredInfos, response);
    }

    private List<AlarmAddedInfo> getRegisteredAlarmInfos(Object alarm) {
        if (alarm instanceof PriceAlarm) {
            return Arrays.stream(AlarmAddedInfo.values())
                    .filter(info -> ((PriceAlarm) alarm).fromInfoIndexList().contains(info.getIndex()))
                    .collect(Collectors.toList());
        } else if (alarm instanceof TargetPriceAlarm) {
            return Arrays.stream(AlarmAddedInfo.values())
                    .filter(info -> ((TargetPriceAlarm) alarm).fromInfoIndexList().contains(info.getIndex()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    private String buildAdditionalInfoHtml(List<AlarmAddedInfo> registeredInfos, SaveCorpInfoListResponse response) {
        StringBuilder html = new StringBuilder();

        for(AlarmAddedInfo info : registeredInfos){
                switch(info){
                    case INVESTMENT_INFO:
                        html.append(buildInvestmentInfoHtml(response));
                        break;
                    case DISCLOSURE:
                        html.append(buildDisclosureInfoHtml(response));
                        break;
                    case MEMO:
                        html.append(buildMemoInfoHtml(response));
                        break;
                }
        }
        
        return html.toString();
    }

    private String buildInvestmentInfoHtml(SaveCorpInfoListResponse response) {
        StringBuilder html = new StringBuilder();
        html.append("""
            <div class='info-box' style='margin-top: 20px;'>
                <h3 style='margin-bottom: 15px;'>투자정보</h3>
                <div style='padding: 12px; background-color: #f5f5f5; border-radius: 4px;'>
                    <div style='margin-bottom: 8px;'><strong>기대수익률:</strong> %.2f%%</div>
                    <div style='margin-bottom: 8px;'><strong>투자기준가:</strong> %,d원</div>
                    <div style='margin-bottom: 8px;'><strong>목표수익률:</strong> %.2f%%</div>
                    <div style='margin-bottom: 8px;'><strong>예상 ROE:</strong> %.2f%%</div>
                </div>
            </div>
            """.formatted(
                response.getExpectedRate(),
                response.getTargetPrice(),
                response.getTargetRate(),
                response.getAfterTenYearsAverageROE()
            ));
        return html.toString();
    }

    private String buildDisclosureInfoHtml(SaveCorpInfoListResponse response) {
        StringBuilder html = new StringBuilder();
        html.append("""
            <div style='margin-top: 20px;'>
                <h3 style='margin-bottom: 15px;'>최근공시</h3>
                <div style='background-color: #f5f5f5; border-radius: 4px; overflow: hidden;'>
                    <table style='width: 100%; border-collapse: collapse;'>
                        <thead>
                            <tr>
                                <th style='background-color: #4c7cff; color: white; padding: 12px; text-align: left;'>번호</th>
                                <th style='background-color: #4c7cff; color: white; padding: 12px; text-align: left;'>보고서명</th>
                                <th style='background-color: #4c7cff; color: white; padding: 12px; text-align: left;'>제출인명</th>
                                <th style='background-color: #4c7cff; color: white; padding: 12px; text-align: left;'>접수일자</th>
                            </tr>
                        </thead>
                        <tbody>
        """);

        int rowNum = 1;
        for (var report : response.getReportList()) {
            html.append(String.format("""
                <tr>
                    <td style='padding: 12px; border-bottom: 1px solid #e0e0e0;'>%d</td>
                    <td style='padding: 12px; border-bottom: 1px solid #e0e0e0;'>%s</td>
                    <td style='padding: 12px; border-bottom: 1px solid #e0e0e0;'>%s</td>
                    <td style='padding: 12px; border-bottom: 1px solid #e0e0e0;'>%s</td>
                </tr>
                """,
                rowNum++,
                report.getReportNm(),
                report.getFlrNm(),
                report.getRceptDt()
            ));
        }

        html.append("</tbody></table></div></div>");
        return html.toString();
    }

    private String buildMemoInfoHtml(SaveCorpInfoListResponse response) {
        StringBuilder html = new StringBuilder();
        html.append("""
            <div style='margin-top: 20px;'>
                <h3 style='margin-bottom: 15px;'>메모장</h3>
                <div style='background-color: #f5f5f5; border-radius: 4px; padding: 12px;'>
        """);

        response.getMemoList().forEach(memo -> {
            html.append(String.format("""
                <div style='margin-bottom: 12px; padding-bottom: 12px; border-bottom: 1px solid #e0e0e0;'>
                    <p style='margin: 0 0 8px 0;'>%s</p>
                    <p style='margin: 0; color: #666; font-size: 0.9rem;'>%s</p>
                </div>
                """,
                memo.getContent(),
                memo.getCreatedAt()
            ));
        });

        html.append("</div></div>");
        return html.toString();
    }
}
