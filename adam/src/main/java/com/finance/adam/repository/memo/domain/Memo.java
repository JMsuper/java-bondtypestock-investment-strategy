package com.finance.adam.repository.memo.domain;

import com.finance.adam.repository.savecorpinfo.domain.SaveCorpInfo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "save_corp_info_id")
    SaveCorpInfo saveCorpInfo;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
