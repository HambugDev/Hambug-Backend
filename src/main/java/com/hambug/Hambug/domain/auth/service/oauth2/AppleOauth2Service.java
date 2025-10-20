package com.hambug.Hambug.domain.auth.service.oauth2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hambug.Hambug.domain.auth.service.JwtService;
import com.hambug.Hambug.domain.oauth.apple.AppleClientSecretGenerator;
import com.hambug.Hambug.domain.oauth.service.impl.AppleUserInfo;
import com.hambug.Hambug.domain.user.dto.UserDto;
import com.hambug.Hambug.domain.user.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppleOauth2Service implements Oauth2Service {

    private final WebClient webClient = WebClient.builder().build();
    private final UserService userService;
    private final JwtService jwtService;
    private final AppleClientSecretGenerator clientSecretGenerator;

    @Value("${spring.security.oauth2.client.registration.apple.redirect-uri:}")
    private String redirectUri; // optional

    private static Map<String, Object> decodeIdToken(String idToken) {
        try {
            String[] parts = idToken.split("\\.");
            if (parts.length < 2) throw new IllegalArgumentException("Invalid id_token");
            byte[] payload = Base64.getUrlDecoder().decode(parts[1]);
            ObjectMapper om = new ObjectMapper();
            return om.readValue(payload, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            throw new IllegalStateException("애플 id_token 파싱 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public UserDto login(String idToken) {
        Map<String, Object> claims = decodeIdToken(idToken);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", claims.get("sub"));
        if (claims.get("email") != null) {
            attributes.put("email", claims.get("email"));
        }
        
        AppleUserInfo appleUserInfo = new AppleUserInfo(attributes);

        return userService.signUpOrLogin(appleUserInfo);
    }

    @Override
    public void unlink(Long userId) {
        String appleRefreshToken = jwtService.getAppleRefreshToken(userId);
        String clientSecret = clientSecretGenerator.generate();
        webClient.post()
                .uri("https://appleid.apple.com/auth/revoke")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("client_id", clientSecretGenerator.getClientId())
                        .with("client_secret", clientSecret)
                        .with("token", appleRefreshToken)
                        .with("token_type_hint", "refresh_token")
                )
                .retrieve()
                .toBodilessEntity()
                .block();

        userService.softDeleteUser(userId);
        jwtService.softDelete(userId);
    }

    @Override
    public String getProviderName() {
        return "apple";
    }

    // Minimal DTO for Apple's token endpoint
    @Data
    private static class AppleTokenResponse {
        private String access_token;
        private String token_type;
        private Long expires_in;
        private String refresh_token;
        private String id_token;

    }
}
