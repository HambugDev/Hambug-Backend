package com.hambug.Hambug.global.constatns;

import java.util.List;

public class Security {
    public static final List<String> WHITELISTED_URLS = List.of(
            "/favicon.ico", "/auth/**",
            "/oauth2/**", "/login/**", "/firebase-messaging-sw.js",
            "/favicon.ico",
            "/static/**",
            "/css/**",
            "/js/**",
            "/images/**",
            "/webjars/**",
            "/v1/terms/**",
            "/v1/token/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api-docs/**",
            "/api-docs",
            "/swagger-resources/**",
            "/webjars/**",
            "/health",
            "/actuator"
    );

    public static final String USER_PATH = "/v1/**";

    public static final String USER_ROLE = "USER";
    
    public static final String KAKAO = "kakao";
    public static final String GOOGLE = "google";
    public static final String NAVER = "naver";

}
