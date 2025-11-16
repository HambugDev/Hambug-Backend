package com.hambug.Hambug.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class Oauth2RequestDTO {

    public record LoginAuthCode(
            @NotBlank(message = "엑세스 토큰은 필수 입니다.")
            String accessToken) {
    }
}
