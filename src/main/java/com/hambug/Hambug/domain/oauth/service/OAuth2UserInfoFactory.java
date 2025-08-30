package com.hambug.Hambug.domain.oauth.service;

import com.hambug.Hambug.domain.oauth.service.impl.AppleUserInfo;
import com.hambug.Hambug.domain.oauth.service.impl.KakaoUserInfo;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.hambug.Hambug.global.constatns.Security.*;


@Component
public class OAuth2UserInfoFactory {

    public Oauth2UserInfo createUserInfo(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case KAKAO -> new KakaoUserInfo(attributes);
            case APPLE -> new AppleUserInfo(attributes);
            case GOOGLE -> new KakaoUserInfo(attributes); // TODO: implement GoogleUserInfo if needed
            default -> throw new IllegalArgumentException("지원하지 않는 OAuth2 제공자입니다: " + registrationId);
        };
    }
}
