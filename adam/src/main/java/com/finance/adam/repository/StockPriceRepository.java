package com.finance.adam.repository;

import com.finance.adam.repository.domain.CorpInfo;
import com.finance.adam.repository.domain.FinanceInfo;
import com.finance.adam.repository.domain.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StockPriceRepository extends JpaRepository<StockPrice,Long> {
    StockPrice findByCorpInfo(CorpInfo corpInfo);
}
