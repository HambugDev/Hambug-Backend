package com.hambug.Hambug.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthService {

    private final OAuth2AuthorizedClientService clientService;

    public String getAccessToken(Authentication authentication, String registrationId) {
        log.info("테스트 합니다. {} , {}", authentication.getName(), registrationId);
        OAuth2AuthorizedClient oauthClient = clientService.loadAuthorizedClient(registrationId, authentication.getName());
        if (oauthClient != null && oauthClient.getAccessToken() != null) {
            return oauthClient.getAccessToken().getTokenValue();
        }
        throw new IllegalStateException("AccessToken not found for user");

    }
}
