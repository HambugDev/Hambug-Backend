package com.hambug.Hambug.global.exception.custom;

import com.hambug.Hambug.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class AuthenticationException extends RuntimeException {

    private final ErrorCode errorCode;


    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AuthenticationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
