package com.finance.adam.service;

import com.finance.adam.exception.CustomException;
import com.finance.adam.exception.ErrorCode;
import com.finance.adam.repository.notification.NotificationRepository;
import com.finance.adam.repository.notification.domain.Notification;
import com.finance.adam.repository.notification.dto.NotificationDto;
import com.finance.adam.util.MyMailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
        List<Notification> notifications = notificationRepository.findAllByAccountIdAndIsDeletedFalseOrderByCreatedAtDesc(accountId);
        return notifications.stream()
                .map(NotificationDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteNotificationById(String accountId, Long notificationId) {
        log.info("Deleting notification with id: {}", notificationId);
        // 알림 조회
        Optional<Notification> optional = notificationRepository.findByIdAndIsDeletedFalse(notificationId);

        // 알림이 없으면 실패
        if(optional.isEmpty()){
            throw new CustomException(ErrorCode.NOT_FOUND);
        }
        // 본인 알림이 아니면 실패
        if(!optional.get().getAccount().getId().equals(accountId)){
            throw new CustomException(ErrorCode.NOT_ALLOWED);
        }

        try {
            notificationRepository.softDeleteNotificationById(notificationId);
            log.debug("Successfully soft deleted notification");
        } catch (Exception e) {
            log.error("Failed to soft delete notification", e);
            throw e;
        }
    }
}
