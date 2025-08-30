package com.hambug.Hambug.domain.oauth.service;

import com.hambug.Hambug.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import static com.hambug.Hambug.global.constatns.Security.APPLE;


@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final OAuth2UserProcessor userProcessor;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        String regId = userRequest.getClientRegistration().getRegistrationId();
        log.info("엥 : {}", userRequest);
        if (APPLE.equalsIgnoreCase(regId)) {
            UserDto userDto = userProcessor.processUser(userRequest);
            return super.loadUser(userRequest);
        }
        return super.loadUser(userRequest);
    }
}