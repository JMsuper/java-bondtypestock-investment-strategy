package com.finance.adam;

import com.finance.adam.service.KoreaFinanceService;
import com.finance.adam.util.OpenAPIUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class AdamApplicationTests {


	@Autowired
	private KoreaFinanceService koreaFinanceService;

	@Test
	void test() {
		koreaFinanceService.getStockCodeList();
	}

}
