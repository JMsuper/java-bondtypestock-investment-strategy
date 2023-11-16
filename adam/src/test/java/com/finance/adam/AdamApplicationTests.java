package com.finance.adam;

import com.finance.adam.calculator.FinancialCalculator;
import com.finance.adam.core.FinanceCore;
import com.finance.adam.datashuttle.CorpListGenerator;
import com.finance.adam.datashuttle.Scrapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
class AdamApplicationTests {

	@Test
	void sampleTest(){
		System.out.println("24.2".matches("^[0-9.]*"));
		System.out.println("dfdf".matches("^[0-9]*"));
	}

	@Test
	void coreTest(){
		new FinanceCore().coreFunc();
	}

	@Test
	void test() {
		// 종목코드 리스트 가져오기
		List<List<String>> arr = CorpListGenerator.generate();
		Scrapper scrapper = new Scrapper();
		// 종목코드 종목명
		for(int i = 0 ; i < arr.size() ; i++){
			Map<String, String> corpInfo = scrapper.getFinancialData(arr.get(i).get(0));
			if(corpInfo.get("EPS") == "0"){
				System.out.println(arr.get(i).get(1) + arr.get(i).get(0) + corpInfo);
			}
		}
		// 하나의 데이터에 대한 재무정보 가져오기는 성공
		// 모든 재무정보 가져올 수 있는지 테스트
		// how to?
		//
	}

	@Test
	void getCorpListTest(){
		CorpListGenerator.generate();
	}

	@Test
	void calculateTest(){
		FinancialCalculator.calculateExpectedReturn(
				FinancialCalculator.calculateBPS(8057,17.07),17.07,72200
		);
	}

	@Test
	void getPriceTest(){
		new Scrapper().getPrice("005930");
	}
}
