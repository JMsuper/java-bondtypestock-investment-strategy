package com.finance.adam.repository.domain;


import com.finance.adam.openapi.publicdataportal.vo.KrxItemInfo;
import com.finance.adam.util.FnInfoName;
import lombok.*;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinanceInfo {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "corp_info_id")
    private CorpInfo corpInfo;

    private Integer year;

    /**
     * 유동자산
     */
    private Long currentAssets;
    /**
     * 비유동자산
     */
    private Long non_currentAssets;
    /**
     * 자산총계
     */
    private Long totalAsset;
    /**
     * 유동부채
     */
    private Long currentLiabilities;
    /**
     * 비유동부채
     */
    private Long nonLiabilities;
    /**
     * 부채총계
     */
    private Long totalLiabilities;
    /**
     * 자본금
     */
    private Long capital;
    /**
     * 이익잉여금
     */
    private Long retainedEarnings;
    /**
     * 자본총계
     */
    private Long totalCapital;
    /**
     * 매출액
     */
    private Long revenue;
    /**
     * 영업이익
     */
    private Long operatingProfit;
    /**
     * 법인세차감전 순이익
     */
    private Long earningsBeforeTax;
    /**
     * 당기순이익
     */
    private Long netIncome;
    /**
     * 당기순이익(손실)
     */
    private Long netLoss;

    public static FinanceInfo fromMap(Map<String, Long> map){
        FinanceInfo financeInfo = new FinanceInfo();
        Set<String> keySet = map.keySet();
        for(String key : keySet){
            String accountNm = FnInfoName.fromValue(key).name();
            Long value = map.get(key);
            try {
                Field field = financeInfo.getClass().getDeclaredField(accountNm);
                field.set(financeInfo,value);
            } catch (NoSuchFieldException e) {
                System.out.println("no such field " + accountNm);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return financeInfo;
    }

}
