package com.hambug.Hambug.global.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hambug.Hambug.global.security.ExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SecurityErrorResponse {

    private String error;
    private String message;
    private String path;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public static SecurityErrorResponse of(ExceptionType exceptionType, String path) {
        return new SecurityErrorResponse(
                exceptionType.name(),
                exceptionType.getMessage(),
                path,
                LocalDateTime.now()
        );
    }
}
