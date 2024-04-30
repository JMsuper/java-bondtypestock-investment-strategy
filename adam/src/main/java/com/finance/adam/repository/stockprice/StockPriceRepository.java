package com.finance.adam.repository.stockprice;

import com.finance.adam.repository.corpinfo.domain.CorpInfo;
import com.finance.adam.repository.stockprice.domain.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StockPriceRepository extends JpaRepository<StockPrice,Long> {
    StockPrice findByCorpInfo(CorpInfo corpInfo);
    StockPrice findByCorpInfoStockCode(String stockCode);
}
