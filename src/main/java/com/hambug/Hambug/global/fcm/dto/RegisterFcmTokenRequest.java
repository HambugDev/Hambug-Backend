package com.hambug.Hambug.global.fcm.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterFcmTokenRequest(
        @NotBlank(message = "토큰은 필수 입니다.")
        String token,
        @NotBlank(message = "플랫폼을 필수 입니다.")
        String platform
) {
}
