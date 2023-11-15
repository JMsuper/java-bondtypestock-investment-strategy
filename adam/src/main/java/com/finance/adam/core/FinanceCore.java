package com.finance.adam.core;

import com.finance.adam.datashuttle.CorpListGenerator;
import com.finance.adam.datashuttle.Scrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FinanceCore {
    public void test(){
        // 종목코드 리스트 가져오기
        List<List<String>> arr = CorpListGenerator.generate();
        Scrapper scrapper = new Scrapper();
        ArrayList<List<String>> corpInfoList = new ArrayList();
        // 종목코드 종목명
        for(int i = 0 ; i < arr.size() ; i++){
            Map<String, String> corpInfo = scrapper.getFinancialData(arr.get(i).get(0));
            if(corpInfo.get("EPS") != "0"){
                corpInfoList.add(arr.get(i));
            }
        }
        // 하나의 데이터에 대한 재무정보 가져오기는 성공
        // 모든 재무정보 가져올 수 있는지 테스트
        // how to?
        //
    }
}
