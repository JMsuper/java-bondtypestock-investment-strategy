package com.finance.adam.repository.notification.dto;

import com.finance.adam.repository.notification.domain.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private Long id;
    private String accountId;
    private String type;
    private String subject;
    private String content;
    private Boolean isRead;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static NotificationDto from(Notification notification){
            return NotificationDto.builder()
                    .id(notification.getId())
                    .accountId(notification.getAccount().getId())
                    .type(notification.getType())
                    .subject(notification.getSubject())
                    .content(notification.getContent())
                    .isRead(notification.getIsRead())
                    .isDeleted(notification.getIsDeleted())
                    .createdAt(notification.getCreatedAt())
                    .updatedAt(notification.getUpdatedAt())
                    .build();
    }
}
