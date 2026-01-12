package com.hambug.Hambug.global.notification.dto;

public record FcmSendRequest(
        String token,
        String title,
        String body,
        String clickAction,
        String imageUrl,
        FcmData data
) {
    public static FcmSendRequest ofBasic(String token, String title, String body) {
        return new FcmSendRequest(token, title, body, "push_click", null, null);
    }

    public static FcmSendRequest ofWithData(String token, String title, String body, FcmData data) {
        return new FcmSendRequest(token, title, body, "push_click", null, data);
    }
}
