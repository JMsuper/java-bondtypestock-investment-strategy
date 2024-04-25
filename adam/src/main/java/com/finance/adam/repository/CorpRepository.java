package com.finance.adam.repository;

import com.finance.adam.repository.domain.CorpInfo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CorpRepository extends JpaRepository<CorpInfo,String> {
    Optional<CorpInfo> findByStockCode(String stockCode);

    @EntityGraph(attributePaths = {"stockPrice"})
    @Query("select c from CorpInfo c")
    List<CorpInfo> findAllWithStockPrice();

    @EntityGraph(attributePaths = {"stockPrice", "financeInfos"})
    @Query("select c from CorpInfo c")
    List<CorpInfo> findAllWithStockPriceAndFinanceInfos();

}
