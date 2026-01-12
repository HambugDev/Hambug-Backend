package com.hambug.Hambug.global.notification.repository;

import com.hambug.Hambug.global.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, CustomNotification {
}
