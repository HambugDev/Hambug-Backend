package com.hambug.Hambug.global.constatns;

import java.util.List;

public class Security {
    public static final List<String> WHITELISTED_URLS = List.of(
            "/",
            "/favicon.ico", "/auth/**",
            "/oauth2/**", "/login/**", "/firebase-messaging-sw.js",
            "/favicon.ico",
            "/static/**",
            "/css/**",
            "/js/**",
            "/images/**",
            "/webjars/**",
            "/api/v1/auth/refresh",
            "/api/v1/auth/login/**",
            "/api/v1/auth/callback",
            "/api/swagger-ui/**",
            "/api/swagger-ui.html",
            "/swagger-ui.html",
            "/api-docs/**",
            "/api-docs",
            "/api-docs",
            "/swagger-resources/**",
            "/webjars/**",
            "/health",
            "/actuator",
            "/admin/api/v1/register",
            "/admin/api/v1/login",
            "/api/v1/login/*",
            "/login"
    );

    public static final List<String> ONLY_SUPER_ADMIN_URLS = List.of(
            "/admin/api/v1/register/manager"
    );

    public static final String USER_PATH = "/api/v1/**";
    public static final String ADMIN_PATH = "/admin/api/v1/**";

    public static final String USER_ROLE = "USER";
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String SUPER_ADMIN_ROLE = "SUPER_ADMIN";

    public static final String KAKAO = "kakao";
    public static final String GOOGLE = "google";
    public static final String NAVER = "naver";
    public static final String APPLE = "apple";

}
