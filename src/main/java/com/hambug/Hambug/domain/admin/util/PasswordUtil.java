package com.hambug.Hambug.domain.admin.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class PasswordUtil {
    private static final SecureRandom random = new SecureRandom();

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 120_000; // strong default
    private static final int KEY_LENGTH = 256; // bits

    public static String generateTempPassword(int length) {
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
                .substring(0, length);
    }

    public static String generateSalt(int length) {
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashWithPbkdf2(String rawPassword, String base64Salt) {
        try {
            byte[] salt = Base64.getDecoder().decode(base64Salt);
            KeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to hash password", e);
        }
    }

    public static boolean matchesPbkdf2(String rawPassword, String base64Salt, String expectedBase64Hash) {
        String computed = hashWithPbkdf2(rawPassword, base64Salt);
        boolean constantTimeEquals = constantTimeEquals(computed, expectedBase64Hash);
        if (!constantTimeEquals) {
            throw new IllegalStateException("Failed to match password");
        }
        return true;
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);
        if (aBytes.length != bBytes.length) return false;
        int result = 0;
        for (int i = 0; i < aBytes.length; i++) {
            result |= aBytes[i] ^ bBytes[i];
        }
        return result == 0;
    }
}
