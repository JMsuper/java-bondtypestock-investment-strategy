package com.finance.adam.repository;

import com.finance.adam.repository.domain.CorpInfo;
import com.finance.adam.repository.domain.FinanceInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinanceInfoRepository extends JpaRepository<FinanceInfo,Long> {

}
