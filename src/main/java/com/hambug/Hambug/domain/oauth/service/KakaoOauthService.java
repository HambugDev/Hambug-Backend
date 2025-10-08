package com.hambug.Hambug.domain.oauth.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hambug.Hambug.domain.oauth.dto.KakaoUserResponse;
import com.hambug.Hambug.domain.user.dto.UserDto;
import com.hambug.Hambug.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoOauthService implements Oauth2Service {

    private final WebClient webClient = WebClient.builder().build();
    private final UserService userService;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret:}")
    private String kakaoClientSecret; // optional
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Override
    public UserDto login(String code) {
        // 1) 액세스 토큰 요청
        BodyInserters.FormInserter<String> form = BodyInserters
                .fromFormData("grant_type", "authorization_code")
                .with("client_id", kakaoClientId)
                .with("redirect_uri", kakaoRedirectUri)
                .with("code", code);

        if (StringUtils.hasText(kakaoClientSecret)) {
            form = form.with("client_secret", kakaoClientSecret);
        }

        TokenResponse token = webClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(form)
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();

        // 2) 사용자 정보 요청
        KakaoUserResponse me = webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .headers(h -> h.setBearerAuth(Objects.requireNonNull(token).getAccessToken()))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(KakaoUserResponse.class)
                .block();

        log.info("정보는 : {}", me.getKakaoAccount());

        return userService.signUpOrLogin(me);
        // 3) 여기서 우리 서비스 로직 수행 (회원 연동/가입/로그인, JWT 발급 등)
    }

    @Override
    public String getProviderName() {
        return "kakao";
    }

    // DTO 예시
    public static class TokenResponse {
        @JsonProperty("token_type")
        private String tokenType;
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("expires_in")
        private Long expiresIn;
        @JsonProperty("refresh_token")
        private String refreshToken;
        @JsonProperty("refresh_token_expires_in")
        private Long refreshTokenExpiresIn;
        @JsonProperty("scope")
        private String scope;

        public String getAccessToken() {
            return accessToken;
        }

        // 기타 getter/setter 필요시 추가
    }

    public static class KakaoUser {
        private Long id;
        @JsonProperty("kakao_account")
        private KakaoAccount kakaoAccount;
        private Properties properties;

        public Long getId() {
            return id;
        }

        public KakaoAccount getKakaoAccount() {
            return kakaoAccount;
        }

        public static class KakaoAccount {
            private String email;

            public String getEmail() {
                return email;
            }
        }

        public static class Properties {
            private String nickname;
        }
    }
}
