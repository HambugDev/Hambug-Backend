package com.hambug.Hambug.domain.auth.service.oauth2;

import com.hambug.Hambug.domain.auth.dto.KakaoUserResponse;
import com.hambug.Hambug.domain.auth.service.JwtService;
import com.hambug.Hambug.domain.user.dto.UserDto;
import com.hambug.Hambug.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

import static com.hambug.Hambug.domain.auth.dto.KakaoUserResponse.TokenResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoOauthService implements Oauth2Service {

    private final WebClient webClient = WebClient.builder().build();
    private final UserService userService;
    private final JwtService jwtService;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret:}")
    private String kakaoClientSecret;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;
    @Value("${kakao.admin-key}")
    private String kakaoAdminKey;

    @Override
    public UserDto login(String code) {
        // 1) 액세스 토큰 요청
        TokenResponse token = getTokenResponse(code);

        log.info("토킁느 : {}", token);

        KakaoUserResponse me = webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .headers(h -> h.setBearerAuth(Objects.requireNonNull(token).getAccessToken()))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(KakaoUserResponse.class)
                .block();
        
        Objects.requireNonNull(me).addToken(token);
        return userService.signUpOrLogin(me);
    }

    @Override
    @Transactional
    public void unlink(Long userId) {
        UserDto userDto = userService.getById(userId);

        WebClient client = WebClient.builder()
                .baseUrl("https://kapi.kakao.com")
                .build();

        try {
            client.post()
                    .uri("/v1/user/unlink")
                    .header("Authorization", "KakaoAK " + kakaoAdminKey)
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                    .body(getUnlinkFormInserter(userDto.getSocialId()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            userService.softDeleteUser(userId);
            jwtService.softDelete(userId);
        } catch (Exception e) {
            log.error("카카오 unlink 요청 실패: {}", e.getMessage(), e);
            throw new RuntimeException("카카오 unlink 실패", e);
        }

    }

    @Override
    public String getProviderName() {
        return "kakao";
    }

    private TokenResponse getTokenResponse(String code) {
        BodyInserters.FormInserter<String> form = getStringFormInserter(code);

        if (StringUtils.hasText(kakaoClientSecret)) {
            form = form.with("client_secret", kakaoClientSecret);
        }

        return webClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(form)
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();
    }

    private BodyInserters.FormInserter<String> getStringFormInserter(String code) {
        log.debug("카카오톡 redirect_uri: {}", kakaoRedirectUri);
        return BodyInserters
                .fromFormData("grant_type", "authorization_code")
                .with("client_id", kakaoClientId)
                .with("redirect_uri", kakaoRedirectUri)
                .with("code", code);
    }

    private BodyInserters.FormInserter<String> getUnlinkFormInserter(String userId) {
        return BodyInserters
                .fromFormData("target_id_type", "user_id")
                .with("target_id", userId);
    }


}
