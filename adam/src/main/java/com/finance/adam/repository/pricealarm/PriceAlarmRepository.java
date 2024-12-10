package com.finance.adam.repository.pricealarm;

import com.finance.adam.repository.account.domain.Account;
import com.finance.adam.repository.pricealarm.domain.PriceAlarm;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PriceAlarmRepository extends JpaRepository<PriceAlarm,Long> {

    @EntityGraph(attributePaths = {"saveCorpInfo", "saveCorpInfo.account", "saveCorpInfo.corpInfo"})
    @Query(value = "select p from PriceAlarm p where p.saveCorpInfo.account = :account")
    List<PriceAlarm> findAllByAccount(Account account);

    List<PriceAlarm> findAllByActive(boolean active);
}
