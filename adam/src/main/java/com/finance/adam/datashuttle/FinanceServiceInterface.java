package com.finance.adam.datashuttle;

import java.util.List;
import java.util.Map;

public interface FinanceServiceInterface {

    // get stock code List in korea stock market
    public Map<String, String> getStockCodeList();

    public List getStockCodeListFromDB();

    // DB에 재무정보 적재
    public boolean updateAllFinancialInfo();

    // get financial data from stock code
    // return Map<String, String> : key = name, value = value
    public Map<String, String> getFinancialData(String stockCode);

    public List<Map<String, String>> getAllFinancialData();



}
