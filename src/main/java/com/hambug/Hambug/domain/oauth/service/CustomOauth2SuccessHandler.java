package com.hambug.Hambug.domain.oauth.service;

import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import com.hambug.Hambug.domain.user.dto.UserDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${jwt.success-path}")
    private String successURI;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UserDto userDto = extractUserDto(authentication);
        response.sendRedirect(buildRedirectUrl(userDto));
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain chain,
                                        Authentication authentication) throws IOException, ServletException {
        onAuthenticationSuccess(request, response, authentication);
    }

    private String buildRedirectUrl(UserDto userDto) {
        return successURI + "?login=" + userDto.isActive() +
                "&accessToken=" +
                URLEncoder.encode(nullSafe(userDto.getAccessToken()), StandardCharsets.UTF_8) +
                "&refreshToken=" +
                URLEncoder.encode(nullSafe(userDto.getRefreshToken()), StandardCharsets.UTF_8);
    }

    private String nullSafe(String v) {
        return v == null ? "" : v;
    }

    private UserDto extractUserDto(Authentication authentication) {
        return ((PrincipalDetails) authentication.getPrincipal()).getUserDto();
    }
}