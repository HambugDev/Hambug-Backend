package com.hambug.Hambug.global.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hambug.Hambug.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private boolean success;
    private String code;
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;


    private ErrorResponse(ErrorCode errorCode) {
        this.success = false;
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.timestamp = LocalDateTime.now();
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }
    
    public static ErrorResponse of(ErrorCode errorCode, String message) {
        ErrorResponse response = new ErrorResponse(errorCode);
        response.message = message;
        return response;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FieldError {
        private String field;
        private String value;
        private String reason;

        private FieldError(String field, String value, String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static FieldError of(String field, String value, String reason) {
            return new FieldError(field, value, reason);
        }

        public static List<FieldError> of(org.springframework.validation.BindingResult bindingResult) {
            List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(error -> FieldError.of(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()
                    ))
                    .toList();
        }
    }
}
