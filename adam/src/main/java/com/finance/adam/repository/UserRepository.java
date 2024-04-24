package com.finance.adam.repository;

import com.finance.adam.repository.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Account, String> {
}
