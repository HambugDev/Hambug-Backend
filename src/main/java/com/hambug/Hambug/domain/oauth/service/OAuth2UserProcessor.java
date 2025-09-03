package com.hambug.Hambug.domain.oauth.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hambug.Hambug.domain.user.dto.UserDto;
import com.hambug.Hambug.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static com.hambug.Hambug.global.constatns.Security.APPLE;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2UserProcessor {

    private final UserService userService;
    private final OAuth2UserInfoFactory userInfoFactory;
    private final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserDto processUser(OAuth2UserRequest userRequest) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Oauth2UserInfo userInfo;

        log.info("여기로 접근 : ={}", userRequest);
        if (APPLE.equalsIgnoreCase(registrationId)) {
            Map<String, Object> attributes = extractAppleAttributes(userRequest);
            userInfo = userInfoFactory.createUserInfo(registrationId, attributes);
        } else {
            OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);
            userInfo = userInfoFactory.createUserInfo(registrationId, oAuth2User.getAttributes());
        }
        return userService.signUpOrLogin(userInfo);
    }

    private Map<String, Object> extractAppleAttributes(OAuth2UserRequest userRequest) {
        Object idTokenObj = userRequest.getAdditionalParameters().get("id_token");
        if (idTokenObj == null) {
            throw new IllegalArgumentException("Apple id_token 이 토큰 응답에 없습니다.");
        }
        String idToken = String.valueOf(idTokenObj);
        String[] parts = idToken.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("유효하지 않은 Apple id_token 형식입니다.");
        }
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        try {
            Map<String, Object> claims = objectMapper.readValue(payloadJson, new TypeReference<Map<String, Object>>() {
            });
            Object iss = claims.get("iss");
            Object aud = claims.get("aud");
            if (!("https://appleid.apple.com".equals(iss))) {
                log.warn("Apple id_token iss 불일치: {}", iss);
            }
            return claims;
        } catch (Exception e) {
            log.error("Apple id_token 파싱 실패", e);
            throw new IllegalArgumentException("Apple id_token 파싱 실패: " + e.getMessage());
        }
    }
}
