package com.hambug.Hambug.global.fcm.service;

import com.google.firebase.messaging.*;
import com.hambug.Hambug.global.fcm.dto.FcmSendRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FcmPushSender {

    public String sendPushNotification(FcmSendRequest req) {
        String token = req.token();
        String title = (req.title() == null || req.title().isBlank()) ? "알림" : req.title();
        String body = (req.body() == null) ? "" : req.body();
        String clickAction = (req.clickAction() == null || req.clickAction().isBlank()) ? "push_click" : req.clickAction();

        Notification.Builder notificationBuilder = Notification.builder()
                .setTitle(title)
                .setBody(body);
        boolean existImageUrl = req.imageUrl() != null && !req.imageUrl().isBlank();
        if (existImageUrl) {
            notificationBuilder.setImage(req.imageUrl());
        }
        Notification notification = notificationBuilder.build();

        AndroidNotification androidNotification = AndroidNotification.builder()
                .setTitle(title)
                .setBody(body)
                .setClickAction(clickAction)
                .build();

        AndroidConfig androidConfig = AndroidConfig.builder()
                .setNotification(androidNotification)
                .build();

        // iOS (APNs) configuration
        ApsAlert apsAlert = ApsAlert.builder()
                .setTitle(title)
                .setBody(body)
                .build();
        Aps.Builder apsBuilder = Aps.builder()
                .setAlert(apsAlert)
                .setSound("default");
        if (!clickAction.isBlank()) {
            apsBuilder.setCategory(clickAction);
        }
        if (existImageUrl) {
            apsBuilder.setMutableContent(true);
        }
        ApnsConfig.Builder apnsConfigBuilder = ApnsConfig.builder()
                .setAps(apsBuilder.build())
                .putHeader("apns-priority", "10");
        if (existImageUrl) {
            apnsConfigBuilder.setFcmOptions(ApnsFcmOptions.builder().setImage(req.imageUrl()).build());
        }
        ApnsConfig apnsConfig = apnsConfigBuilder.build();

        Message.Builder messageBuilder = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .setAndroidConfig(androidConfig)
                .setApnsConfig(apnsConfig);
        if (req.data() != null) {
            messageBuilder.putAllData(req.data().toMap());
        }
        Message message = messageBuilder.build();

        try {
            return FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException exception) {
            log.error("Fcm 메시지 전송 실패 : {}", exception.getMessage());
            throw new RuntimeException(exception);
        }
    }
}
