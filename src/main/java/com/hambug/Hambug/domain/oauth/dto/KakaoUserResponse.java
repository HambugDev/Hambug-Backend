package com.hambug.Hambug.domain.oauth.dto;

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

    @Override
    public String getId() {
        return String.valueOf(id);
    }

    @Override
    public String getEmail() {
        return kakaoAccount.getEmail();
    }

    @Override
    public String getName() {
        return kakaoAccount.getProfile().getNickname();
    }

    @Override
    public LoginType getLoginType() {
        return LoginType.KAKAO;
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
}