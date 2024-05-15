package com.finance.adam.util;

public enum AlarmAddedInfo {
    /**
     * 투자정보(기대수익률, 투자기준가, 목표수익률, 예상 ROE)
     */
    INVESTMENT_INFO,
    /**
     * 최근 공시 5건
     */
    DISCLOSURE,
    /**
     * 메모장
     */
    MEMO;

    public int getIndex() {
        return this.ordinal();
    }

    public static AlarmAddedInfo valueOfFrom(int index) {
        return AlarmAddedInfo.values()[index];
    }
}
