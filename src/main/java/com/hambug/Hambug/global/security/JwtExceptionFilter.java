package com.hambug.Hambug.global.security;

import com.hambug.Hambug.global.response.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {
    private final SecurityResponseHandler securityResponseHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            log.warn("JWT 토큰이 만료되었습니다: {}", e.getMessage());
            securityResponseHandler.handleSecurityError(
                    request,
                    response,
                    ErrorCode.JWT_TOKEN_EXPIRED,
                    HttpStatus.UNAUTHORIZED,
                    e
            );
        } catch (JwtException e) {
            log.warn("유효하지 않은 JWT 토큰입니다: {}", e.getMessage());
            securityResponseHandler.handleSecurityError(
                    request,
                    response,
                    ErrorCode.JWT_TOKEN_INVALID,
                    HttpStatus.UNAUTHORIZED,
                    e
            );
        } catch (EntityNotFoundException e) {
            securityResponseHandler.handleSecurityError(
                    request,
                    response,
                    ErrorCode.NOT_FOUND_ENTITY,
                    HttpStatus.NOT_FOUND,
                    e
            );
        } catch (Exception e) {
            log.error("필터에서 예상치 못한 오류가 발생했습니다: {}", e.getMessage(), e);
            securityResponseHandler.handleSecurityError(
                    request,
                    response,
                    ErrorCode.SERVER_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e
            );
        }
    }
}
