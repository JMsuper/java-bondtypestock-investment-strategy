package com.finance.adam.service;

import com.finance.adam.repository.notification.domain.Notification;
import com.finance.adam.util.MyMailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MyMailSender mailSender;

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
}
