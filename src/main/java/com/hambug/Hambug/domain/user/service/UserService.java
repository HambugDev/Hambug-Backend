package com.hambug.Hambug.domain.user.service;

import com.hambug.Hambug.domain.oauth.service.Oauth2UserInfo;
import com.hambug.Hambug.domain.user.dto.UserDto;

public interface UserService {

    UserDto signUpOrLogin(Oauth2UserInfo userInfo);
}
