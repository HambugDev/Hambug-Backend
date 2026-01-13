package com.hambug.Hambug.global.notification.service;

import com.hambug.Hambug.domain.user.dto.UserDto;
import com.hambug.Hambug.domain.user.service.UserService;
import com.hambug.Hambug.global.event.CommentCreatedEvent;
import com.hambug.Hambug.global.event.LikeCreatedEvent;
import com.hambug.Hambug.global.event.UserLogoutFcmEvent;
import com.hambug.Hambug.global.notification.dto.FcmData;
import com.hambug.Hambug.global.notification.dto.FcmDataType;
import com.hambug.Hambug.global.notification.dto.FcmSendRequest;
import com.hambug.Hambug.global.notification.dto.RegisterFcmTokenRequest;
import com.hambug.Hambug.global.notification.entity.FcmDeviceToken;
import com.hambug.Hambug.global.notification.repository.FcmDeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmDeviceTokenService {

    private final FcmDeviceTokenRepository fcmRepo;
    private final UserService userService;
    private final FcmPushSender pushSender;

    @Transactional
    public void registerOrUpdate(Long userId, RegisterFcmTokenRequest req) {

        FcmDeviceToken token = fcmRepo.findByUserId(userId)
                .map(existing -> updateIfChanged(existing, req))
                .orElseGet(() -> createNewToken(userId, req));

        FcmDeviceToken saved = fcmRepo.save(token);

        sendLoginCompletePush(saved);
    }

    @Async
    @EventListener
    @Transactional
    public void deactivate(UserLogoutFcmEvent logoutFcmEvent) {
        fcmRepo.findByUserId(logoutFcmEvent.getUserId())
                .ifPresent(FcmDeviceToken::deactivate);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendCommentPush(CommentCreatedEvent event) {
        fcmRepo.findByUserId(event.boardAuthorId())
                .ifPresent(token -> {
                    try {
                        FcmSendRequest sendRequest = FcmSendRequest.ofWithData(
                                token.getToken(),
                                "새 댓글 알림",
                                event.commentAuthorName() + "님이 댓글을 남겼습니다: " + event.commentContent(),
                                FcmData.of(FcmDataType.COMMENT_NOTIFICATION)
                        );
                        pushSender.sendPushNotification(sendRequest);
                    } catch (Exception e) {
                        log.warn("댓글 푸시 알림 전송 실패: {}", e.getMessage());
                    }
                });
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendLikePush(LikeCreatedEvent event) {
        fcmRepo.findByUserId(event.boardAuthorId())
                .ifPresent(token -> {
                    try {
                        FcmSendRequest sendRequest = FcmSendRequest.ofWithData(
                                token.getToken(),
                                event.likeAuthorNickname(),
                                "회원님의 게시물을 좋아합니다",
                                FcmData.of(FcmDataType.LIKE_NOTIFICATION)
                        );
                        pushSender.sendPushNotification(sendRequest);
                    } catch (Exception e) {
                        log.warn("좋아요 푸시 알림 전송 실패: {}", e.getMessage());
                    }
                });
    }
    
    private FcmDeviceToken updateIfChanged(FcmDeviceToken existing, RegisterFcmTokenRequest req) {
        if (!existing.isSameAs(req.token(), req.platform())) {
            existing.applyRegistration(req);
        }
        return existing;
    }

    private FcmDeviceToken createNewToken(Long userId, RegisterFcmTokenRequest req) {
        UserDto userDto = userService.getById(userId);
        return FcmDeviceToken.of(userDto, req);
    }

    private void sendLoginCompletePush(FcmDeviceToken token) {
        try {
            FcmSendRequest sendReq = FcmSendRequest.ofWithData(
                    token.getToken(),
                    "로그인 완료",
                    "성공적으로 로그인되었습니다.",
                    FcmData.of(FcmDataType.LOGIN_COMPLETE)
            );
            pushSender.sendPushNotification(sendReq);
        } catch (RuntimeException ex) {
            log.warn("FCM push failed after registration: {}", ex.getMessage());
        }
    }
}

