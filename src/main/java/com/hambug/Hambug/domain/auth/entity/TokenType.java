package com.hambug.Hambug.domain.auth.entity;

public enum TokenType {
    ACCESS_TOKEN(0),
    REFRESH_TOKEN(1),
    APPLE_REFRESH_TOKEN(2),
    KAKAO_REFRESH_TOKEN(3);

    private final int code;

    TokenType(int code) {
        this.code = code;
    }

    public static TokenType fromCode(int code) {
        for (TokenType t : values()) {
            if (t.code == code) return t;
        }
        throw new IllegalArgumentException("Invalid token type code: " + code);
    }

    public int getCode() {
        return code;
    }
}
