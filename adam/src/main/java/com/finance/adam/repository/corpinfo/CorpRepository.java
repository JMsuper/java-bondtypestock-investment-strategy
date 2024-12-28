package com.finance.adam.repository.corpinfo;

import com.finance.adam.repository.corpinfo.domain.CorpInfo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CorpRepository extends JpaRepository<CorpInfo,String> {
    Optional<CorpInfo> findByStockCode(String stockCode);

    @Query("""
        SELECT ci, sp
        FROM CorpInfo ci
        JOIN FETCH ci.stockPrice sp
    """)
    List<CorpInfo> findAllWithStockPrice();

    @EntityGraph(attributePaths = {"stockPrice", "financeInfos"})
    @Query("select c from CorpInfo c")
    List<CorpInfo> findAllWithStockPriceAndFinanceInfos();

}
