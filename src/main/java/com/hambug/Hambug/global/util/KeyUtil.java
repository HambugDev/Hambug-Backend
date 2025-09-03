package com.hambug.Hambug.global.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
public class KeyUtil {

    private static final String RSA_ALGORITHM = "RSA";
    private static final String BEGIN_MARKER = "-----BEGIN";
    private static final String END_MARKER = "-----END";

    private KeyUtil() {
    }

    /**
     * InputStream에서 Private Key 로드
     */
    public static PrivateKey loadPrivateKey(InputStream inputStream) throws IOException, GeneralSecurityException {
        String keyContent = extractKeyContent(inputStream);
        byte[] keyBytes = decodeBase64Key(keyContent, "Private key");
        return createKeyFromSpec(new PKCS8EncodedKeySpec(keyBytes), PrivateKey.class);
    }

    /**
     * InputStream에서 Public Key 로드
     */
    public static PublicKey loadPublicKey(InputStream inputStream) throws IOException, GeneralSecurityException {
        String keyContent = extractKeyContent(inputStream);
        byte[] keyBytes = decodeBase64Key(keyContent, "Public key");
        return createKeyFromSpec(new X509EncodedKeySpec(keyBytes), PublicKey.class);
    }

    /**
     * InputStream에서 키 내용 추출 (공통 메서드)
     */
    private static String extractKeyContent(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder keyBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                if (isPemHeader(line)) {
                    continue;
                }
                keyBuilder.append(line.trim());
            }

            String keyContent = keyBuilder.toString();
            validateKeyContent(keyContent);

            return keyContent;
        }
    }

    private static boolean isPemHeader(String line) {
        return line.contains(BEGIN_MARKER) || line.contains(END_MARKER);
    }

    private static void validateKeyContent(String keyContent) {
        if (keyContent == null || keyContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Key content is empty or null");
        }
    }

    private static byte[] decodeBase64Key(String keyContent, String keyType) throws GeneralSecurityException {
        try {
            return Base64.getDecoder().decode(keyContent);
        } catch (IllegalArgumentException e) {
            String errorMessage = String.format("Invalid Base64 encoding in %s: %s", keyType, e.getMessage());
            log.error(errorMessage);
            throw new GeneralSecurityException(errorMessage, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T createKeyFromSpec(KeySpec keySpec, Class<T> keyType) throws GeneralSecurityException {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);

            if (keyType == PrivateKey.class) {
                return (T) keyFactory.generatePrivate(keySpec);
            } else if (keyType == PublicKey.class) {
                return (T) keyFactory.generatePublic(keySpec);
            } else {
                throw new IllegalArgumentException("Unsupported key type: " + keyType.getSimpleName());
            }
        } catch (GeneralSecurityException e) {
            String errorMessage = String.format("Failed to generate %s: %s", keyType.getSimpleName(), e.getMessage());
            log.error(errorMessage);
            throw new GeneralSecurityException(errorMessage, e);
        }
    }

    /**
     * 파일 경로에서 Private Key 로드 (편의 메서드)
     */
    public static PrivateKey loadPrivateKeyFromResource(String resourcePath) throws IOException, GeneralSecurityException {
        try (InputStream inputStream = KeyUtil.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            return loadPrivateKey(inputStream);
        }
    }

    /**
     * 파일 경로에서 Public Key 로드 (편의 메서드)
     */
    public static PublicKey loadPublicKeyFromResource(String resourcePath) throws IOException, GeneralSecurityException {
        try (InputStream inputStream = KeyUtil.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            return loadPublicKey(inputStream);
        }
    }
}
