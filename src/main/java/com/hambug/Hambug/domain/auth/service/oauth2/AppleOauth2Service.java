package com.hambug.Hambug.domain.auth.service.oauth2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppleOauth2Service implements Oauth2Service {

    private final WebClient webClient = WebClient.builder().build();
    private final UserService userService;
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
    public UserDto login(String authorizationCode) {
        String clientSecret = clientSecretGenerator.generate();

        BodyInserters.FormInserter<String> form = BodyInserters
                .fromFormData("grant_type", "authorization_code")
                .with("code", authorizationCode)
                .with("client_id", clientSecretGenerator.getClientId())
                .with("client_secret", clientSecret);
        if (redirectUri != null && !redirectUri.isBlank()) {
            form = form.with("redirect_uri", redirectUri);
        }

        AppleTokenResponse token = webClient.post()
                .uri("https://appleid.apple.com/auth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(form)
                .retrieve()
                .bodyToMono(AppleTokenResponse.class)
                .block();

        log.info("Apple token response: {}", token);

        Map<String, Object> claims = decodeIdToken(Objects.requireNonNull(token).id_token);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", claims.get("sub"));
        if (claims.get("email") != null) {
            attributes.put("email", claims.get("email"));
        }
        // Include provider refresh_token so it can be persisted
        if (token != null && token.refresh_token != null) {
            attributes.put("refresh_token", token.refresh_token);
        }

        return userService.signUpOrLogin(new AppleUserInfo(attributes));
    }

    @Override
    public void unlink(Long userId) {

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
