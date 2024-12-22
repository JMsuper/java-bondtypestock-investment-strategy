package com.finance.adam.repository.savecorpinfo;

import com.finance.adam.repository.account.domain.Account;
import com.finance.adam.repository.savecorpinfo.domain.SaveCorpInfo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SaveCorpInfoRepository extends JpaRepository<SaveCorpInfo, Long> {

    Optional<SaveCorpInfo> findByCorpInfoCorpCodeAndAccountId(String corpCode, String id);

    Optional<SaveCorpInfo> findByIdAndAccountId(Long saveCorpInfoId, String id);

    //TODO : Memo Entity N + 1 문제 해결 필요
    @EntityGraph(attributePaths = {"corpInfo", "corpInfo.stockPrice", "corpInfo.financeInfos"})
    @Query("""
        SELECT s 
        FROM SaveCorpInfo s 
        WHERE s.account = :account 
        ORDER BY s.id
    """)
    List<SaveCorpInfo> findAllByAccount(Account account);

    int countByAccountId(String id);
}
