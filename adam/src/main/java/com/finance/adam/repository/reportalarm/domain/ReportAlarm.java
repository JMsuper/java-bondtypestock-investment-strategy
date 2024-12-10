package com.finance.adam.repository.reportalarm.domain;

import com.finance.adam.repository.savecorpinfo.domain.SaveCorpInfo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportAlarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private ReportType reportType;

    @ColumnDefault("true")
    @Builder.Default()
    private boolean active = true;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    @JoinColumn(name = "save_corp_info_id")
    private SaveCorpInfo saveCorpInfo;
}
