package com.hambug.Hambug.global.notification.repository;

import com.hambug.Hambug.global.notification.entity.Notification;
import org.springframework.data.domain.Slice;

public interface CustomNotification {

    Slice<Notification> findByUserIdSlice(Long userId, Long lastId, int limit);
}
