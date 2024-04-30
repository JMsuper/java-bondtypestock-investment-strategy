package com.finance.adam.repository.financeinfo;

import com.finance.adam.repository.financeinfo.domain.FinanceInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FinanceInfoRepository extends JpaRepository<FinanceInfo,Long> {
    Optional<FinanceInfo> findByCorpInfoCorpCodeAndYear(String corpCode, int year);
}
