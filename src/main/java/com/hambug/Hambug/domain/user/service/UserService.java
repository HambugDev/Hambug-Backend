package com.hambug.Hambug.domain.user.service;

import com.hambug.Hambug.domain.oauth.service.Oauth2UserInfo;
import com.hambug.Hambug.domain.user.dto.UserDto;
import com.hambug.Hambug.domain.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserDto signUpOrLogin(Oauth2UserInfo userInfo);

    UserDto getById(Long userId);

    User getReferenceById(Long userId);

    UserDto updateNickname(Long userId, Long authUserId, String nickname);

    UserDto updateProfileImage(Long userId, Long authUserId, MultipartFile file);

    void softDeleteUser(Long userId);
}
