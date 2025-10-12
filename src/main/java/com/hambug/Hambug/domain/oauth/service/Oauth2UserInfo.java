package com.hambug.Hambug.domain.oauth.service;

import com.hambug.Hambug.domain.user.entity.LoginType;

public interface Oauth2UserInfo {

    String getId();

    String getEmail();

    String getName();

    LoginType getLoginType();

    String getRefreshToken();
}
