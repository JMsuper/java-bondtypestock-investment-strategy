package com.finance.adam.repository.targetpricealarm;

import com.finance.adam.repository.account.domain.Account;
import com.finance.adam.repository.savecorpinfo.domain.SaveCorpInfo;
import com.finance.adam.repository.targetpricealarm.domain.TargetPriceAlarm;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TargetPriceAlarmRepository extends JpaRepository<TargetPriceAlarm,Long> {

    @EntityGraph(attributePaths = {"saveCorpInfo", "saveCorpInfo.account", "saveCorpInfo.corpInfo"})
    @Query(value = "select t from TargetPriceAlarm t where t.saveCorpInfo.account = :account")
    List<TargetPriceAlarm> findAllByAccount(Account account);
}
