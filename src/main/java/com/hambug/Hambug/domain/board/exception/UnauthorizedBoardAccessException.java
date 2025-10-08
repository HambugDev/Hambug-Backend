package com.hambug.Hambug.domain.board.exception;

import com.hambug.Hambug.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class UnauthorizedBoardAccessException extends RuntimeException {

    private final ErrorCode errorCode;

    public UnauthorizedBoardAccessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public UnauthorizedBoardAccessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}