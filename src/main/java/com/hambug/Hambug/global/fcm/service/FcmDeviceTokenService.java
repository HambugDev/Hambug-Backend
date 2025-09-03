package com.hambug.Hambug.global.fcm.service;

import com.hambug.Hambug.domain.user.dto.UserDto;
import com.hambug.Hambug.domain.user.service.UserService;
import com.hambug.Hambug.global.event.UserLogoutFcmEvent;
import com.hambug.Hambug.global.fcm.dto.FcmData;
import com.hambug.Hambug.global.fcm.dto.FcmDataType;
import com.hambug.Hambug.global.fcm.dto.FcmSendRequest;
import com.hambug.Hambug.global.fcm.dto.RegisterFcmTokenRequest;
import com.hambug.Hambug.global.fcm.entity.FcmDeviceToken;
import com.hambug.Hambug.global.fcm.repository.FcmDeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

