package com.hambug.Hambug.global.notification.service;

import com.hambug.Hambug.domain.board.repository.BoardRepository;
import com.hambug.Hambug.domain.like.repository.BoardLikeRepository;
import com.hambug.Hambug.domain.user.entity.User;
import com.hambug.Hambug.domain.user.service.UserService;
import com.hambug.Hambug.global.event.CommentCreatedEvent;
import com.hambug.Hambug.global.event.LikeCreatedEvent;
import com.hambug.Hambug.global.event.LikeDeletedEvent;
import com.hambug.Hambug.global.notification.dto.FcmDataType;
import com.hambug.Hambug.global.notification.dto.NotificationResponseDTO;
import com.hambug.Hambug.global.notification.entity.Notification;
import com.hambug.Hambug.global.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardRepository boardRepository;

    @Transactional(readOnly = true)
    public NotificationResponseDTO.NotificationAllResponse getNotifications(Long userId, Long lastId, int limit) {
        Slice<Notification> slice = notificationRepository.findByUserIdSlice(userId, lastId, limit);

        List<NotificationResponseDTO.NotificationResponse> content = slice.getContent().stream()
                .map(NotificationResponseDTO.NotificationResponse::from)
                .toList();

        Long nextCursorId = content.isEmpty() ? null : content.get(content.size() - 1).id();
        boolean nextPage = slice.hasNext();

        return new NotificationResponseDTO.NotificationAllResponse(content, nextCursorId, nextPage);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveCommentNotification(CommentCreatedEvent event) {
        User receiver = User.toEntity(userService.getById(event.boardAuthorId()));
        String thumbnailUrl = boardRepository.findById(event.boardId())
                .map(board -> board.getImageUrls().isEmpty() ? null : board.getImageUrls().get(0))
                .orElse(null);

        Notification notification = Notification.builder()
                .receiver(receiver)
                .title("새 댓글 알림")
                .content(event.commentAuthorName() + "님이 댓글을 남겼습니다: " + event.commentContent())
                .type(FcmDataType.COMMENT_NOTIFICATION)
                .targetId(event.boardId())
                .thumbnailUrl(thumbnailUrl)
                .build();

        notificationRepository.save(notification);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLikeNotification(LikeCreatedEvent event) {
        User receiver = User.toEntity(userService.getById(event.boardAuthorId()));

        // 1시간 이내의 동일한 게시글에 대한 좋아요 알림이 있는지 확인
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        Optional<Notification> existingNotification = notificationRepository.findFirstByReceiverAndTargetIdAndTypeAndCreatedAtAfterOrderByCreatedAtDesc(
                receiver, event.boardId(), FcmDataType.LIKE_NOTIFICATION, oneHourAgo
        );

        long likeCount = boardLikeRepository.countByBoardId(event.boardId());
        String title = event.likeAuthorNickname();
        String content = "회원님의 게시물을 좋아합니다";

        if (likeCount > 1) {
            title = event.likeAuthorNickname() + "님 외 " + (likeCount - 1) + "명";
            content = "회원님의 게시물을 좋아합니다";
        }

        String thumbnailUrl = boardRepository.findById(event.boardId())
                .map(board -> board.getImageUrls().isEmpty() ? null : board.getImageUrls().get(0))
                .orElse(null);

        if (existingNotification.isPresent()) {
            Notification notification = existingNotification.get();
            notification.updateContent(title, content);
            notificationRepository.save(notification);
        } else {
            Notification notification = Notification.builder()
                    .receiver(receiver)
                    .title(title)
                    .content(content)
                    .type(FcmDataType.LIKE_NOTIFICATION)
                    .targetId(event.boardId())
                    .thumbnailUrl(thumbnailUrl)
                    .build();
            notificationRepository.save(notification);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateLikeNotificationOnDelete(LikeDeletedEvent event) {
        User receiver = User.toEntity(userService.getById(event.userId()));

        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        Optional<Notification> existingNotification = notificationRepository.findFirstByReceiverAndTargetIdAndTypeAndCreatedAtAfterOrderByCreatedAtDesc(
                receiver, event.boardId(), FcmDataType.LIKE_NOTIFICATION, oneHourAgo
        );

        existingNotification.ifPresent(notification -> {
            long likeCount = boardLikeRepository.countByBoardId(event.boardId());

            if (likeCount <= 0) {
                notificationRepository.delete(notification);
            } else {
                // 가장 최근에 좋아요를 누른 사람의 정보를 가져옵니다.
                String latestNickname = boardLikeRepository.findFirstByBoardIdOrderByCreatedAtDesc(event.boardId())
                        .map(boardLike -> boardLike.getUser().getNickname())
                        .orElse("누군가");

                String title;
                String content = "회원님의 게시물을 좋아합니다";

                if (likeCount == 1) {
                    title = latestNickname;
                } else {
                    title = latestNickname + "님 외 " + (likeCount - 1) + "명";
                }

                notification.updateContent(title, content);
                notificationRepository.save(notification);
            }
        });
    }
}
