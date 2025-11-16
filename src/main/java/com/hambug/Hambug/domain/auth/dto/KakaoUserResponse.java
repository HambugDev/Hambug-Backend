package com.hambug.Hambug.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hambug.Hambug.domain.oauth.service.Oauth2UserInfo;
import com.hambug.Hambug.domain.user.entity.LoginType;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class KakaoUserResponse implements Oauth2UserInfo {

    private Long id;

    @JsonProperty("connected_at")
    private String connectedAt;

    private Properties properties;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    private TokenResponse token;

    @Override
    public String getId() {
        return String.valueOf(id);
    }

    @Override
    public LoginType getLoginType() {
        return LoginType.KAKAO;
    }

    @Override
    public String getRefreshToken() {
        if (token == null || token.getRefreshToken() == null) {
            return null;
        }
        return token.getRefreshToken();
    }

    public void addToken(TokenResponse token) {
        this.token = token;
    }

    @Getter
    @ToString
    public static class Properties {
        private String nickname;
        @JsonProperty("profile_image")
        private String profileImage;
        @JsonProperty("thumbnail_image")
        private String thumbnailImage;
    }

    @Getter
    @ToString
    public static class KakaoAccount {
        private String email;
        private Profile profile;
    }

    @Getter
    @ToString
    public static class Profile {
        private String nickname;
        @JsonProperty("profile_image_url")
        private String profileImageUrl;
        @JsonProperty("thumbnail_image_url")
        private String thumbnailImageUrl;
    }

    @Getter
    @ToString
    public static class TokenResponse {
        @JsonProperty("token_type")
        private String tokenType;
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("expires_in")
        private Long expiresIn;
        @JsonProperty("refresh_token")
        private String refreshToken;
        @JsonProperty("refresh_token_expires_in")
        private Long refreshTokenExpiresIn;
        @JsonProperty("scope")
        private String scope;

    }
}