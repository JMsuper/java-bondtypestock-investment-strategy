package com.finance.adam.openapi.publicdataportal.vo;

import java.util.HashMap;
import java.util.List;

/**
 * * 공공데이터포털 API <br>
 * - 금융위원회_KRX 상장종목정보<br>
 * - (24-01-29)
 */
public class KrxResponseBody {

    private int pageNo;

    /**
     * 한 페이지의 결과 수
     */
    private int numOfRows;

    /**
     * 전체 데이터의 총 수
     */
    private int totalCount;

    private HashMap<String,List<KrxItemInfo>> items;

    public int getPageNo(){return pageNo;}

    public int getNumOfRows() {
        return numOfRows;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public List<KrxItemInfo> getItems() {
        return items.get("item");
    }
}
