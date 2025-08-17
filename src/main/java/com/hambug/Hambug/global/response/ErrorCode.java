package com.hambug.Hambug.global.response;

import lombok.Getter;

@Getter
public enum ErrorCode {

    JWT_TOKEN_INVALID("J001", "유효하지 않은 JWT 토큰입니다"),
    JWT_TOKEN_EXPIRED("J002", "만료된 JWT 토큰입니다"),
    JWT_TOKEN_MISSING("J003", "JWT 토큰이 누락되었습니다."),
    NOT_FOUND_ENTITY("COM001", "해당 엔티티를 찾을수 없습니다."),
    ALREADY_ENTITY("COM002", "해당 엔티티는 이미 존재합니다."),
    SERVER_ERROR("S001", "서버 오류가 발생했습니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
