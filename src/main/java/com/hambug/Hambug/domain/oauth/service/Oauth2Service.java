package com.hambug.Hambug.domain.oauth.service;

import com.hambug.Hambug.domain.user.dto.UserDto;

public interface Oauth2Service {

    UserDto login(String authorizationCode);

    String getProviderName();
}
