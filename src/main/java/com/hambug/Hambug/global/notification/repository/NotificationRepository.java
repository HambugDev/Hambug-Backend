package com.hambug.Hambug.global.notification.repository;

import com.hambug.Hambug.domain.user.entity.User;
import com.hambug.Hambug.global.notification.dto.FcmDataType;
import com.hambug.Hambug.global.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, CustomNotification {
    Optional<Notification> findFirstByReceiverAndTargetIdAndTypeAndCreatedAtAfterOrderByCreatedAtDesc(
            User receiver, Long targetId, FcmDataType type, LocalDateTime time
    );
}
