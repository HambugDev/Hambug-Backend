package com.hambug.Hambug.domain.oauth.apple;

import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
@RequiredArgsConstructor
public class AppleAuthorizationCodeTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    private final AppleClientSecretGenerator clientSecretGenerator;

    private final DefaultAuthorizationCodeTokenResponseClient delegate = new DefaultAuthorizationCodeTokenResponseClient();

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        OAuth2AuthorizationCodeGrantRequestEntityConverter defaultConverter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();
        this.delegate.setRequestEntityConverter(grantRequest -> {
            RequestEntity<?> entity = defaultConverter.convert(grantRequest);
            String regId = grantRequest.getClientRegistration().getRegistrationId();
            if (!"apple".equalsIgnoreCase(regId)) {
                return entity;
            }
            @SuppressWarnings("unchecked")
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>((MultiValueMap<String, String>) entity.getBody());
            body.set("client_secret", clientSecretGenerator.generate());
            return new RequestEntity<>(body, entity.getHeaders(), entity.getMethod(), entity.getUrl());
        });
        return this.delegate.getTokenResponse(authorizationGrantRequest);
    }
}
