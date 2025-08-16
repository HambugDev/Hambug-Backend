package com.hambug.Hambug.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hambug.Hambug.global.response.SecurityErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityResponseHandler {

    private final ObjectMapper objectMapper;

    public void handleSecurityError(HttpServletRequest request,
                                    HttpServletResponse response,
                                    ExceptionType exceptionType,
                                    HttpStatus status,
                                    Exception exception) throws IOException {

        log.warn("보안 예외 발생: {} - {} ({})",
                request.getRequestURI(),
                exceptionType.getMessage(),
                exception.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status.value());

        SecurityErrorResponse errorResponse = SecurityErrorResponse.of(
                exceptionType,
                request.getRequestURI()
        );

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

}
