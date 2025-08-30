package com.hambug.Hambug.domain.auth.service;

import com.hambug.Hambug.domain.oauth.apple.AppleClientSecretGenerator;
import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import com.hambug.Hambug.domain.user.entity.User;
import com.hambug.Hambug.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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
    private final UserRepository userRepository;
    private final AppleClientSecretGenerator clientSecretGenerator;

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
            String response = client.post()
                    .uri("/v1/user/unlink")
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("카카오 unlink 응답: {}", response);
            softDeleteUser(userId);
        } catch (Exception e) {
            log.error("카카오 unlink 요청 실패: {}", e.getMessage(), e);
            throw new RuntimeException("카카오 unlink 실패", e);
        }
    }

    @Transactional
    public void appleUnlink(Long userId, String refreshToken) {
        WebClient client = WebClient.builder()
                .baseUrl("https://appleid.apple.com")
                .build();
        try {
            String response = client.post()
                    .uri("/auth/revoke")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .bodyValue("client_id=" + clientSecretGenerator.getClientId()
                            + "&client_secret=" + clientSecretGenerator.generate()
                            + "&token=" + refreshToken
                            + "&token_type_hint=refresh_token")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("애플 unlink 응답: {}", response);

            softDeleteUser(userId);

        } catch (Exception e) {
            log.error("애플 unlink 요청 실패: {}", e.getMessage(), e);
            throw new RuntimeException("애플 unlink 실패", e);
        }
    }

    private void softDeleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을수 없습니다."));
        user.setActive(false);
        user.markDeleted();
        // JPA dirty checking에 의해 트랜잭션 커밋 시 자동 반영
        log.info("사용자 {} 소프트 삭제 처리(deleted_at 설정, isActive=false)", userId);
    }
}
