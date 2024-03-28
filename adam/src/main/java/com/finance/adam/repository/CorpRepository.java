package com.finance.adam.repository;

import com.finance.adam.repository.domain.CorpInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CorpRepository extends JpaRepository<CorpInfo,String> {
    Optional<CorpInfo> findByStockCode(String stockCode);
}
