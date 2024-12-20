package com.finance.adam.repository.stockprice;

import com.finance.adam.repository.corpinfo.domain.CorpInfo;
import com.finance.adam.repository.stockprice.domain.StockPrice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;


public interface StockPriceRepository extends JpaRepository<StockPrice,Long> {

    @Query("select s from StockPrice s join fetch s.corpInfo")
    List<StockPrice> findAllWithCorpInfo();

    StockPrice findByCorpInfo(CorpInfo corpInfo);

    StockPrice findByCorpInfoStockCode(String stockCode);
}
