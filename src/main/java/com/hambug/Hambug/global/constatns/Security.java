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
            "/api/v1/tokens/**",
            "/api/swagger-ui/**",
            "/api/swagger-ui.html",
            "/swagger-ui.html",
            "/api-docs/**",
            "/api-docs",
            "/api-docs",
            "/swagger-resources/**",
            "/webjars/**",
            "/health",
            "/actuator"
    );

    public static final String USER_PATH = "/api/v1/**";
    public static final String ADMIN_PATH = "/api/v1/admin/**";

    public static final String USER_ROLE = "USER";
    public static final String ADMIN_ROLE = "ADMIN";

    public static final String KAKAO = "kakao";
    public static final String GOOGLE = "google";
    public static final String NAVER = "naver";

}
