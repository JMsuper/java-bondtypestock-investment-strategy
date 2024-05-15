package com.finance.adam.repository.memo;

import com.finance.adam.repository.memo.domain.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemoRepository extends JpaRepository<Memo, Long> {

    Optional<Memo> findByIdAndAccountId(Long memoId, String id);
}
