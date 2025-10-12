package com.hambug.Hambug.domain.auth.service.oauth2;

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
}
