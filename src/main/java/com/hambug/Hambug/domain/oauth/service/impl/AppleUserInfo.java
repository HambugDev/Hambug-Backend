package com.hambug.Hambug.domain.oauth.service.impl;

import com.hambug.Hambug.domain.oauth.service.Oauth2UserInfo;
import com.hambug.Hambug.domain.user.entity.LoginType;

import java.util.Map;

public class AppleUserInfo implements Oauth2UserInfo {

    private final Map<String, Object> attributes;

    public AppleUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getId() {
        Object sub = attributes.get("sub");
        return sub == null ? null : String.valueOf(sub);
    }

    @Override
    public LoginType getLoginType() {
        return LoginType.APPLE;
    }

    @Override
    public String getRefreshToken() {
        Object rt = attributes.get("refresh_token");
        return rt == null ? null : String.valueOf(rt);
    }
}
