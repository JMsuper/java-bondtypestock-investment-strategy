package com.finance.adam.repository.domain;

import com.finance.adam.util.FnInfoName;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class BalanceSheet {

    @Id
    private String stockCode;

    /**
     * 접수 번호(14자리) : 공시뷰어 연결에 이용
     */
    private String reportCode;

    /**
     * 사업 연도
     */
    private String baseYear;

    @Column(name = "current_value" + "hello")
    private Long currentAsset1;

}
