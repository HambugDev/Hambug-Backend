package com.hambug.Hambug.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Slf4j
@Configuration
public class FcmConfig {

    private final ResourceLoader resourceLoader;
    @Value("${firebase.credentials-location:}")
    private String credentialsLocation; // e.g., file:C:/secrets/firebase-sa.json or classpath:fcm-service-account.json
    @Value("${firebase.credentials-base64:}")
    private String credentialsBase64;   // base64-encoded JSON string (optional)

    public FcmConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void initialize() {
        try {
            GoogleCredentials credentials = resolveCredentials();
            if (credentials == null) {
                log.warn("Firebase Credentials not configured. Skipping Firebase initialization.");
                return;
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase initialized successfully.");
            } else {
                log.info("FirebaseApp already initialized. Skipping re-initialization.");
            }
        } catch (IOException exception) {
            log.error("FCM 연결 실패 : {}", exception.getMessage(), exception);
        }
    }

    private GoogleCredentials resolveCredentials() throws IOException {
        if (credentialsLocation != null && !credentialsLocation.isBlank()) {
            log.info("Using firebase.credentials-location: {}", credentialsLocation);
            Resource resource = resourceLoader.getResource(credentialsLocation);
            if (resource.exists()) {
                try (var in = resource.getInputStream()) {
                    return GoogleCredentials.fromStream(in);
                }
            } else {
                log.warn("Credentials resource not found at: {}", credentialsLocation);
            }
        }

        if (credentialsBase64 != null && !credentialsBase64.isBlank()) {
            log.info("Using firebase.credentials-base64 from configuration.");
            byte[] decoded = java.util.Base64.getDecoder().decode(credentialsBase64);
            return GoogleCredentials.fromStream(new ByteArrayInputStream(decoded));
        }

        String gac = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        if (gac != null && !gac.isBlank()) {
            log.info("Using GOOGLE_APPLICATION_CREDENTIALS from environment.");
            return GoogleCredentials.getApplicationDefault();
        }

        return null;
    }
}
