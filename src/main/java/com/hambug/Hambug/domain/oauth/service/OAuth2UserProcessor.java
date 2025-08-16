package com.hambug.Hambug.domain.oauth.service;

import com.hambug.Hambug.domain.user.dto.UserDto;
import com.hambug.Hambug.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2UserProcessor {

    private final UserService userService;
    private final OAuth2UserInfoFactory userInfoFactory;
    private final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();

    public UserDto processUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Oauth2UserInfo userInfo = userInfoFactory.createUserInfo(registrationId, oAuth2User.getAttributes());
        return userService.signUpOrLogin(userInfo);
    }
}
