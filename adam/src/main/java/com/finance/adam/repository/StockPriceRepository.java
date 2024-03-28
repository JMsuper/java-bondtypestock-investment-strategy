package com.finance.adam.repository;

import com.finance.adam.repository.domain.CorpInfo;
import com.finance.adam.repository.domain.FinanceInfo;
import com.finance.adam.repository.domain.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface StockPriceRepository extends JpaRepository<StockPrice,Long> {
    StockPrice findByCorpInfo(CorpInfo corpInfo);
    StockPrice findByCorpInfoStockCode(String stockCode);
}
