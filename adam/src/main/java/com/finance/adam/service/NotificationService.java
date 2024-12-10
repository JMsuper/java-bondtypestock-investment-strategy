package com.finance.adam.service;

import com.finance.adam.repository.notification.domain.Notification;
import com.finance.adam.util.MyMailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MyMailSender mailSender;

    public void handleNotification(Notification notification) {
        // Notification 객체를 받아 이메일 전송
        mailSender.sendNotificationEmail(notification);
    }
}
