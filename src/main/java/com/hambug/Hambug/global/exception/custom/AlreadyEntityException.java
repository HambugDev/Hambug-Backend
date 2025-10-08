package com.hambug.Hambug.global.exception.custom;

import com.hambug.Hambug.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class AlreadyEntityException extends RuntimeException {

    private final ErrorCode errorCode;

    public AlreadyEntityException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AlreadyEntityException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
