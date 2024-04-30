package com.finance.adam.openapi.dart.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OpenDartReportDTO {

    /**
     * 법인구분 : Y(유가), K(코스닥), N(코넥스), E(기타)
     */
    private String corpCls;

    /**
     * 종목명(법인명)
     * 공시대상회사의 종목명(상장사) 또는 법인명(기타법인)
     */
    private String corpName;

    /**
     * 종목코드
     * 상장종목의 종목코드(6자리) 또는 고유번호(8자리)
     */
    private String corpCode;

    /**
     * 종목코드
     * 상장종목의 종목코드(6자리) 또는 고유번호(8자리)
     */
    private String stockCode;

    /**
     * 보고서명
     * 공시구분+보고서명+기타정보
     */
    private String reportNm;

    /**
     * 접수번호
     * 접수번호(14자리)
     * ※ 공시뷰어 연결에 이용예시
     * - PC용 : https://dart.fss.or.kr/dsaf001/main.do?rcpNo=접수번호
     */
    private String rceptNo;

    /**
     * 공시 제출인명
     */
    private String flrNm;

    /**
     * 접수일자
     * 공시 접수일자(YYYYMMDD)
     */
    private String rceptDt;

    /**
     * 비고
     * 조합된 문자로 각각은 아래와 같은 의미가 있음
     * 유 : 본 공시사항은 한국거래소 유가증권시장본부 소관임
     * 코 : 본 공시사항은 한국거래소 코스닥시장본부 소관임
     * 채 : 본 문서는 한국거래소 채권상장법인 공시사항임
     * 넥 : 본 문서는 한국거래소 코넥스시장 소관임
     * 공 : 본 공시사항은 공정거래위원회 소관임
     * 연 : 본 보고서는 연결부분을 포함한 것임
     * 정 : 본 보고서 제출 후 정정신고가 있으니 관련 보고서를 참조하시기 바람
     * 철 : 본 보고서는 철회(간주)되었으니 관련 철회신고서(철회간주안내)를 참고하시기 바람
     */
    private String rm;
}
