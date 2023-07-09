package com.finance.adam.service;

import java.util.Map;

public interface FinanceServiceInterface {

    // get stock code List in korea stock market
    public String[] getStockCodeList();

    // get financial data from stock code
    // return Map<String, String> : key = name, value = value
    public Map<String, Integer> getFinancialData(String stockCode);

    // get stock price from stock code
    public int getStockPrice(String stockCode);

    // check stock is valid to buy
    public boolean isValidToBuy(String stockCode);


}
