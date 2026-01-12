package com.hambug.Hambug.global.notification.service;

import com.hambug.Hambug.domain.user.entity.User;
import com.hambug.Hambug.domain.user.service.UserService;
import com.hambug.Hambug.global.event.CommentCreatedEvent;
import com.hambug.Hambug.global.notification.dto.FcmDataType;
import com.hambug.Hambug.global.notification.entity.Notification;
import com.hambug.Hambug.global.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveCommentNotification(CommentCreatedEvent event) {
        User receiver = User.toEntity(userService.getById(event.boardAuthorId()));

        Notification notification = Notification.builder()
                .receiver(receiver)
                .title("새 댓글 알림")
                .content(event.commentAuthorName() + "님이 댓글을 남겼습니다: " + event.commentContent())
                .type(FcmDataType.COMMENT_NOTIFICATION)
                .targetId(event.boardId())
                .build();

        notificationRepository.save(notification);
    }

}
