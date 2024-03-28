package com.finance.adam.repository;

import com.finance.adam.repository.domain.CorpInfo;
import com.finance.adam.repository.domain.FinanceInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FinanceInfoRepository extends JpaRepository<FinanceInfo,Long> {
    Optional<FinanceInfo> findByCorpInfoCorpCodeAndYear(String corpCode, int year);
}
