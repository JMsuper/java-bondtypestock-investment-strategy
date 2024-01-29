package com.finance.adam.openapi.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

/**
 * url : https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15094775
 * 금융위원회_KRX 상장종목정보 API 명세 기준
 * (24-01-29)
 */
public class KrxResponseBodyVO {

//    public KrxResponseBodyVO(int numOfRows, int totalCount, HashMap<String,Object> items){
//        this.numOfRows = numOfRows;
//        this.totalCount = totalCount;
//        this. = items.get("item");
//    }

    /**
     * 한 페이지의 결과 수
     */
    private int numOfRows;

    /**
     * 전체 데이터의 총 수
     */
    private int totalCount;

    private HashMap<String,List<KrxItemInfoVO>> items;

    public int getNumOfRows() {
        return numOfRows;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public List<KrxItemInfoVO> getItems() {
        return items.get("item");
    }
}
