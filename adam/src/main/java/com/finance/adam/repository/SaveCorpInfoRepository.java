package com.finance.adam.repository;

import com.finance.adam.repository.domain.Account;
import com.finance.adam.repository.domain.SaveCorpInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SaveCorpInfoRepository extends JpaRepository<SaveCorpInfo, Long> {

    Optional<SaveCorpInfo> findByCorpInfoCorpCodeAndAccountId(String corpCode, String id);

    List<SaveCorpInfo> findAllByAccount(Account account);

    int countByAccountId(String id);
}
