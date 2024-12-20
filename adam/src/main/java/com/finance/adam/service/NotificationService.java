package com.finance.adam.service;

import com.finance.adam.repository.notification.NotificationRepository;
import com.finance.adam.repository.notification.domain.Notification;
import com.finance.adam.repository.notification.dto.NotificationDto;
import com.finance.adam.util.MyMailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MyMailSender mailSender;
    private final NotificationRepository notificationRepository;

    public void handleNotification(Notification notification) {
        log.info("Handling notification for user: {}, type: {}", notification.getAccount().getId(), notification.getType());
        try {
            mailSender.sendNotificationEmail(notification);
            log.debug("Successfully sent notification email");
        } catch (Exception e) {
            log.error("Failed to send notification email", e);
            throw e;
        }
    }

    public List<NotificationDto> getAllNotificationsByAccountId(String accountId) {
        log.info("Getting all notifications for account: {}", accountId);
        List<Notification> notifications = notificationRepository.findAllByAccountIdAndIsDeletedFalse(accountId);
        return notifications.stream()
                .map(NotificationDto::from)
                .collect(Collectors.toList());
    }

    public void deleteNotificationById(Long notificationId) {
        log.info("Deleting notification with id: {}", notificationId);
        try {
            notificationRepository.deleteNotificationById(notificationId);
            log.debug("Successfully deleted notification");
        } catch (Exception e) {
            log.error("Failed to delete notification", e);
            throw e;
        }
    }
}
