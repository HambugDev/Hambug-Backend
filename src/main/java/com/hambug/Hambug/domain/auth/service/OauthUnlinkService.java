package com.hambug.Hambug.domain.auth.service;

import com.hambug.Hambug.domain.oauth.apple.AppleClientSecretGenerator;
import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import com.hambug.Hambug.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class OauthUnlinkService {

    private final OAuthService oAuthService;
    private final UserService userService;
    private final AppleClientSecretGenerator clientSecretGenerator;
    private final JwtService jwtService;

    @Transactional
    public void unlink(PrincipalDetails principalDetails, Authentication authentication) {
        if (principalDetails.getUser().isKakao()) {
            kakaoUnlink(principalDetails.getUser().getUserId(), authentication);
            return;
        }
        appleUnlink(principalDetails.getUser().getUserId(),
                oAuthService.getAccessToken(authentication, "apple"));
    }

    @Transactional
    public void kakaoUnlink(Long userId, Authentication authentication) {
        WebClient client = WebClient.builder()
                .baseUrl("https://kapi.kakao.com")
                .build();
        String accessToken = oAuthService.getAccessToken(authentication, "kakao");

        try {
            client.post()
                    .uri("/v1/user/unlink")
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
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

    @Transactional
    public void appleUnlink(Long userId, String accessToken) {
        WebClient client = WebClient.builder()
                .baseUrl("https://appleid.apple.com")
                .build();
        try {
            String response = client.post()
                    .uri("/auth/revoke")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .bodyValue("client_id=" + clientSecretGenerator.getClientId()
                            + "&client_secret=" + clientSecretGenerator.generate()
                            + "&token=" + accessToken
                            + "&token_type_hint=access_token")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            userService.softDeleteUser(userId);

        } catch (Exception e) {
            log.error("애플 unlink 요청 실패: {}", e.getMessage(), e);
            throw new RuntimeException("애플 unlink 실패", e);
        }
    }
}
