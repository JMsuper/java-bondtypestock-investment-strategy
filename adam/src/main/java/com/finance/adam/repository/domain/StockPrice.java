package com.finance.adam.repository.domain;


import com.finance.adam.openapi.publicdataportal.vo.KrxItemInfo;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockPrice {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * 회사 정보, 주식 코드
     */
    @OneToOne
    @JoinColumn(name = "corp_info_id")
    private CorpInfo corpInfo;

    /**
     * 종가
     */
    private Long closingPrice;

    /**
     * 차이
     */
    private Long difference;

    /**
     * 등락률
     */
    private double fluctuationRate;

    /**
     * 시가
     */
    private Long openingPrice;

    /**
     * 고가
     */
    private Long highPrice;

    /**
     * 저가
     */
    private Long lowPrice;

    /**
     * 거래량
     */
    private Long volume;

    /**
     * 거래 가치
     */
    private Long tradingValue;

    /**
     * 시가 총액
     */
    private Long marketCap;

    /**
     * 상장 주식 수
     */
    private Long listedShares;

    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Updated at
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
