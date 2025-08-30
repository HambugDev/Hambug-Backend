package com.hambug.Hambug.domain.auth.service;

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
    public void appleUnlink(Long userId) {
        // 애플 토큰 revoke 과정을 별도로 구현할 수 있지만,
        // 현재는 계정 연동 해제 시 사용자 소프트 삭제를 수행합니다.
        softDeleteUser(userId);
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
