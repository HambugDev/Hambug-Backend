package com.hambug.Hambug.domain.oauth.dto;

import jakarta.validation.constraints.NotBlank;

public class Oauth2RequestDTO {

    public record LoginAuthCode(
            @NotBlank(message = "인가 코드는 필수 입니다.")
            String authorizationCode) {
    }
}
