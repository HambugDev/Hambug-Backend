package com.hambug.Hambug.global.notification.dto;

import com.hambug.Hambug.global.notification.entity.Notification;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationResponseDTO {

    public record NotificationAllResponse(
            List<NotificationResponse> content,
            Long lastId,
            Boolean hasNext
    ) {
    }

    public record NotificationResponse(
            Long id,
            String title,
            String content,
            FcmDataType type,
            Long targetId,
            boolean isRead,
            LocalDateTime createdAt
    ) {
        public static NotificationResponse from(Notification notification) {
            return new NotificationResponse(
                    notification.getId(),
                    notification.getTitle(),
                    notification.getContent(),
                    notification.getType(),
                    notification.getTargetId(),
                    notification.isRead(),
                    notification.getCreatedAt()
            );
        }
    }
}
