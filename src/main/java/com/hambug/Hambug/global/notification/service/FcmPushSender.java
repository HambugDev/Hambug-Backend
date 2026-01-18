package com.hambug.Hambug.global.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.hambug.Hambug.global.notification.dto.FcmSendRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FcmPushSender {

    public String sendPushNotification(FcmSendRequest req) {
        String token = req.token();
        String title = (req.title() == null || req.title().isBlank()) ? "알림" : req.title();
        String body = (req.body() == null) ? "" : req.body();
        log.info("fcm 리퀘스트 : {}", req);

        Message.Builder messageBuilder = Message.builder()
                .setToken(token);

        if (req.data() != null) {
            messageBuilder.putAllData(req.data().toMap(title, body));
        } else {
            messageBuilder.putAllData(java.util.Map.of("title", title, "body", body));
        }
        Message message = messageBuilder.build();

        try {
            return FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException exception) {
            log.error("Fcm 메시지 전송 실패: {}", exception.getMessage(), exception);
            throw new RuntimeException(exception);
        }
    }
}
