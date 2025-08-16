package com.hambug.Hambug.domain.oauth.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import jakarta.servlet.http.HttpSession;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOauth2FailHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // Prevent redirect loops: respond once with 401 and clear any auth attributes/session
        log.warn("OAuth2 authentication failed. Message: {}", exception.getMessage(), exception);

        // Clear any leftover authentication attributes saved by Spring Security
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            // Invalidate session to avoid any repeated attempts tied to session state
            session.invalidate();
        }

        // Return a single failure response without redirect
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.getWriter().write("{\"error\":\"oauth2_authentication_failed\",\"message\":\"OAuth2 인증에 실패했습니다. 다시 시도해 주세요.\"}");
        response.getWriter().flush();
    }
}
