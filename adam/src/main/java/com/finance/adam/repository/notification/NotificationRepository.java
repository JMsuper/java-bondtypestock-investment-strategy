package com.finance.adam.repository.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finance.adam.repository.notification.domain.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
}
