package com.finance.adam.datashuttle.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StockInfo {
    private String code;
    private String EPS;
    private String BPS;
    private String ROE;
    private String EXP_RET;
}
