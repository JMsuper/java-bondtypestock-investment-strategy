package com.finance.adam.core;

import com.finance.adam.calculator.FinancialCalculator;
import com.finance.adam.datashuttle.CorpListGenerator;
import com.finance.adam.datashuttle.Scrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinanceCore {
    public void coreFunc(){
        // 종목코드 리스트 가져오기
        List<List<String>> arr = CorpListGenerator.generate();
        Scrapper scrapper = new Scrapper();
        ArrayList<Map> corpInfoMapList = new ArrayList();
        // 종목코드 종목명
        for(int i = 0 ; i < arr.size() ; i++){
            Map<String, String> corpInfo = scrapper.getFinancialData(arr.get(i).get(0));
            if(corpInfo.get("EPS") != "0" && !corpInfo.get("EPS").isEmpty()){
                Map map = new HashMap<>();
                map.put("code",arr.get(i).get(0));
                map.put("name",arr.get(i).get(1));
                map.put("EPS",corpInfo.get("EPS"));
                map.put("BPS",corpInfo.get("BPS"));
                map.put("ROE",corpInfo.get("ROE"));
                map.put("price",scrapper.getPrice(arr.get(i).get(0)));
                corpInfoMapList.add(map);
            }
        }

        for(int i = 0 ; i < corpInfoMapList.size() ; i++){
            Map<String, String> corpInfo = corpInfoMapList.get(i);
            double expectedReturn = FinancialCalculator.calculateExpectedReturn(
                    Double.parseDouble(corpInfo.get("BPS")),
                    Double.parseDouble(corpInfo.get("ROE")),
                    Double.parseDouble(corpInfo.get("price"))
            );
            System.out.println(corpInfo.get("name") + " " + corpInfo.get("code") + " " + String.format("%2f",expectedReturn));
        }

        // 기대수익률 계산

        // 하나의 데이터에 대한 재무정보 가져오기는 성공
        // 모든 재무정보 가져올 수 있는지 테스트
        // how to?
        //
    }
}
