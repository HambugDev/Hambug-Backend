package com.hambug.Hambug.domain.auth.service.oauth2;

import com.hambug.Hambug.global.exception.ErrorCode;
import com.hambug.Hambug.global.exception.custom.AuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Oauth2ServiceFactory {

    private final List<Oauth2Service> services;

    public Oauth2Service getService(String provider) {
        return services.stream()
                .filter(s -> s.getProviderName().equalsIgnoreCase(provider))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported provider: " + provider));
    }

    public void validateProvider(String provider, String token) {
        boolean isJwt = token != null && token.split("\\.").length == 3;

        if (provider.equalsIgnoreCase("apple")) {
            if (!isJwt) {
                throw new AuthenticationException(ErrorCode.INVALID_PROVIDER_TOKEN_FORMAT);
            }
            return;
        }

        if (provider.equalsIgnoreCase("kakao")) {
            if (isJwt) {
                throw new AuthenticationException(ErrorCode.INVALID_PROVIDER_TOKEN_FORMAT);
            }
        }
    }
}
