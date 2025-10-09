package com.hambug.Hambug.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    JWT_TOKEN_INVALID("J001", "유효하지 않은 JWT 토큰입니다."),
    JWT_TOKEN_EXPIRED("J002", "만료된 JWT 토큰입니다."),
    JWT_TOKEN_MISSING("J003", "JWT 토큰이 존재하지 않습니다."),
    NOT_FOUND_ENTITY("COM001", "해당 데이터를 찾을 수 없습니다."),
    ALREADY_ENTITY("COM002", "이미 존재하는 데이터입니다."),
    SERVER_ERROR("S001", "서버 오류가 발생했습니다."),
    BOARD_NOT_FOUND("B001", "게시글을 찾을 수 없습니다."),
    UNAUTHORIZED_BOARD_ACCESS("B002", "게시글에 접근할 권한이 없습니다."),
    COMMENT_NOT_FOUND("C001", "댓글을 찾을 수 없습니다."),
    COMMENT_BOARD_MISMATCH("C002", "게시글과 댓글 정보가 일치하지 않습니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
