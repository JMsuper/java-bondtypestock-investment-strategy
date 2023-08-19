package com.finance.adam;

import com.finance.adam.datashuttle.KoreaFinanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AdamApplicationTests {


	@Autowired
	private KoreaFinanceService koreaFinanceService;

	@Test
	void test() {

		koreaFinanceService.getStockCodeList();
//		Scrapper scrapper = new Scrapper();
//		Map<String,String> map = scrapper.getFinancialData("000020");
//		double EPS = Double.valueOf(map.get("EPS"));
//		double ROE = Double.valueOf(map.get("ROE"));
//		double BPS = FinancialCalculations.calculateBPS(EPS,ROE);
//		map.put("BPS",String.valueOf(BPS));
//		Iterator<String> iterator = map.keySet().iterator();
//		while(iterator.hasNext()){
//			String key = iterator.next();
//			System.out.println("key : " + key);
//			System.out.println("value : " + map.get(key));
//		}
	}

}
