package com.finance.adam.repository.notification.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.finance.adam.repository.account.domain.Account;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    // 알림 유형 - 주가, 목표가, 공시
    // 주가 : pricealarm
    // 목표가 : targetpricealarm
    // 공시 : report
    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String subject;

    // 알림 내용
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 알림 읽음 여부
    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;
        
    // 알림 삭제 여부
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    // 알림 생성 시간
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 알림 수정 시간
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
