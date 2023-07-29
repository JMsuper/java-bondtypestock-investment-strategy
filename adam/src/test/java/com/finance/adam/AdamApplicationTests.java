package com.finance.adam;

import com.finance.adam.service.KoreaFinanceService;
import com.finance.adam.util.FinancialCalculations;
import com.finance.adam.util.OpenAPIUtil;
import com.finance.adam.util.Scrapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

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
