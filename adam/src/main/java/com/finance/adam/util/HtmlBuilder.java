package com.finance.adam.util;

import com.finance.adam.openapi.dart.dto.DartReportDTO;
import com.finance.adam.repository.pricealarm.domain.PriceAlarm;
import com.finance.adam.repository.savecorpinfo.domain.SaveCorpInfo;
import com.finance.adam.repository.stockprice.dto.StockPriceInfoDTO;
import com.finance.adam.repository.targetpricealarm.domain.TargetPriceAlarm;
import org.springframework.stereotype.Component;

@Component
public class HtmlBuilder {

    private static final String FOOTER_HTML = """
        <div style='margin-top: 16px; padding-top: 12px; border-top: 1px solid #e0e0e0; text-align: center; font-size: 0.875rem; color: #757575;'>
            <p>자세한 정보는 <a href='https://snowball-stock.vercel.app/' target='_blank' style='color: #1976d2; text-decoration: none;'>Snowball Stock</a>에서 확인하세요.</p>
        </div>
    """;

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

        html.append(FOOTER_HTML);
        html.append("</div>");
        return html.toString();
    }

    public String buildTargetPriceAlarmHtml(TargetPriceAlarm alarm, StockPriceInfoDTO stockPriceInfo) {
        StringBuilder html = new StringBuilder();
        html.append("<div style='padding: 16px; background-color: #ffffff; border: 1px solid #e0e0e0; border-radius: 8px; font-family: Arial, sans-serif;'>");

        html.append("<h3 style='color: #1976d2; margin: 0; font-size: 1.5rem;'>")
                .append(stockPriceInfo.getStockName())
                .append(" 목표가 도달</h3>");

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

        html.append(FOOTER_HTML);
        html.append("</div>");
        return html.toString();
    }
}
