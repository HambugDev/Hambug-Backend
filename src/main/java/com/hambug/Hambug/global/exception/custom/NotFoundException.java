package com.hambug.Hambug.global.exception.custom;

import com.hambug.Hambug.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    private final ErrorCode errorCode;

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public NotFoundException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}