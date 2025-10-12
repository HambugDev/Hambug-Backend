package com.hambug.Hambug.domain.oauth.service.impl;

import com.hambug.Hambug.domain.oauth.service.Oauth2UserInfo;
import com.hambug.Hambug.domain.user.entity.LoginType;

import java.util.Map;

public class KakaoUserInfo implements Oauth2UserInfo {

    private final Map<String, Object> attribute;
    private final Map<String, Object> kakaoProperties;
    private final Map<String, String> kakao_account;

    public KakaoUserInfo(Map<String, Object> attribute) {
        this.attribute = attribute;
        this.kakaoProperties = (Map<String, Object>) attribute.get("properties");
        this.kakao_account = (Map<String, String>) attribute.get("kakao_account");
    }

    @Override
    public String getId() {
        Long id = (Long) attribute.get("id");
        return String.valueOf(id);
    }

    @Override
    public LoginType getLoginType() {
        return LoginType.KAKAO;
    }

    @Override
    public String getRefreshToken() {
        return "";
    }

}