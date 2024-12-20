package com.finance.adam.repository.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.finance.adam.repository.notification.domain.Notification;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByAccountIdAndIsDeletedFalse(String accountId);
    
    @Modifying
    @Query("UPDATE Notification n SET n.isDeleted = true WHERE n.id = :notificationId")
    void deleteNotificationById(@Param("notificationId") Long notificationId);
}
