package com.hambug.Hambug.domain.board.exception;

import com.hambug.Hambug.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class BoardNotFoundException extends RuntimeException {

    private final ErrorCode errorCode;

    public BoardNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BoardNotFoundException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}