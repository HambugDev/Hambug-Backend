package com.hambug.Hambug.global.security;

public enum ExceptionType {
    SERVER_ERROR("서버 오류가 발생했습니다."),
    ACCESS_DENIED("접근 가능한 권한이 없습니다."),
    JWT_TOKEN_EXPIRED("JWT 토큰이 만료되었습니다."),
    JWT_TOKEN_INVALID("유효하지 않은 JWT 토큰입니다."),
    JWT_TOKEN_MISSING("JWT 토큰이 누락되었습니다."),
    JWT_TOKEN_MALFORMED("잘못된 형식의 JWT 토큰입니다."),
    UNAUTHORIZED("접근할수 없는 페이지 입니다.");

    private final String message;

    ExceptionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
