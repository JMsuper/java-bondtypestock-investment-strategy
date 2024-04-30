package com.finance.adam.repository.savecorpinfo.domain;

import com.finance.adam.repository.account.domain.Account;
import com.finance.adam.repository.corpinfo.domain.CorpInfo;
import com.finance.adam.repository.memo.domain.Memo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class SaveCorpInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "corp_info_id")
    private CorpInfo corpInfo;

    @OneToMany(mappedBy = "saveCorpInfo")
    private List<Memo> memoList;

    @Builder.Default
    private Float targetRate = 0.0f;

    private Float afterTenYearsAverageROE;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
