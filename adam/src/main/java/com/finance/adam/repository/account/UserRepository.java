package com.finance.adam.repository.account;

import com.finance.adam.repository.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Account, String> {
}
