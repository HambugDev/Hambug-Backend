package com.hambug.Hambug.domain.oauth.apple;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleClientSecretGenerator {

    private final ResourceLoader resourceLoader;

    @Value("${apple.oauth2.team-id}")
    private String teamId;

    @Value("${apple.oauth2.client-id}")
    private String clientId;

    @Value("${apple.oauth2.key-id}")
    private String keyId;

    @Value("${apple.oauth2.private-key-path}")
    private String privateKeyPath; // e.g., classpath:keys/AuthKey_xxx.p8

    public String generate() {
        try {
            PrivateKey privateKey = loadEcPrivateKey(privateKeyPath);
            Instant now = Instant.now();
            String token = Jwts.builder()
                    .setHeaderParam("kid", keyId)
                    .setIssuer(teamId)
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(now.plus(5, ChronoUnit.MINUTES)))
                    .setAudience("https://appleid.apple.com")
                    .setSubject(clientId)
                    .signWith(privateKey, SignatureAlgorithm.ES256)
                    .compact();
            log.debug("Generated Apple client_secret JWT (exp 5m)");
            return token;
        } catch (Exception e) {
            throw new IllegalStateException("애플 client_secret 생성 실패: " + e.getMessage(), e);
        }
    }

    private PrivateKey loadEcPrivateKey(String location) throws Exception {
        Resource resource = resourceLoader.getResource(location);
        try (InputStream is = resource.getInputStream(); BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("-----")) continue;
                sb.append(line.trim());
            }
            byte[] pkcs8 = Base64.getDecoder().decode(sb.toString());
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pkcs8);
            KeyFactory kf = KeyFactory.getInstance("EC");
            return kf.generatePrivate(spec);
        }
    }
}
