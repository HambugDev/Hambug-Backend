package com.hambug.Hambug.domain.auth.service.oauth2;

import com.hambug.Hambug.domain.user.dto.UserDto;

public interface Oauth2Service {

    UserDto login(String authorizationCode);

    void unlink(Long userId);

    String getProviderName();
}
