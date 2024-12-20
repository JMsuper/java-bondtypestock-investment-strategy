package com.finance.adam.controller;

import com.finance.adam.auth.dto.AccountDto;
import com.finance.adam.repository.notification.dto.NotificationDto;
import com.finance.adam.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    // 전체 알림 조회
    @GetMapping
    public List<NotificationDto> getNotifications(@AuthenticationPrincipal AccountDto accountDto){
        return notificationService.getAllNotificationsByAccountId(accountDto.getId());
    }

    // 특정 알림 삭제( 알림 ID를 기준으로)
    @DeleteMapping("/{notificationId}")
    public void deleteNotificationById(
            @AuthenticationPrincipal AccountDto accountDto,
            @PathVariable Long notificationId){
        notificationService.deleteNotificationById(accountDto.getId(), notificationId);
    }
}
