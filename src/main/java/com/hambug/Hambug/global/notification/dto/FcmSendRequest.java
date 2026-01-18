package com.hambug.Hambug.global.notification.dto;

public record FcmSendRequest(
        String token,
        String title,
        String body,
        FcmData data
) {
    public static FcmSendRequest ofBasic(String token, String title, String body) {
        return new FcmSendRequest(token, title, body, null);
    }

    public static FcmSendRequest ofWithData(String token, String title, String body, FcmData data) {
        return new FcmSendRequest(token, title, body, data);
    }
}
