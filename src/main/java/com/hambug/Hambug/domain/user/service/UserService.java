package com.hambug.Hambug.domain.user.service;

import com.hambug.Hambug.domain.oauth.service.Oauth2UserInfo;
import com.hambug.Hambug.domain.user.dto.UserDto;
import com.hambug.Hambug.domain.user.entity.User;

public interface UserService {

    UserDto signUpOrLogin(Oauth2UserInfo userInfo);

    UserDto getById(Long userId);

    User getReferenceById(Long userId);

    UserDto updateNickname(Long userId, Long authUserId, String nickname);
}
