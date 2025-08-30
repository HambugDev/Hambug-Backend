package com.hambug.Hambug.global.config;

import com.hambug.Hambug.domain.oauth.apple.AppleAuthorizationCodeTokenResponseClient;
import com.hambug.Hambug.domain.oauth.service.CustomOauth2FailHandler;
import com.hambug.Hambug.domain.oauth.service.CustomOauth2SuccessHandler;
import com.hambug.Hambug.domain.oauth.service.CustomOauth2UserService;
import com.hambug.Hambug.domain.oauth.service.CustomOidcUserService;
import com.hambug.Hambug.global.security.JwtAuthenticationFilter;
import com.hambug.Hambug.global.security.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import static com.hambug.Hambug.global.constatns.Security.*;

@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final CustomOauth2UserService customOauth2UserService;
    private final CustomOauth2SuccessHandler customOauth2SuccessHandler;
    private final CustomOauth2FailHandler customOauth2FailHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final CustomOidcUserService customOidcUserService;
    private final AppleAuthorizationCodeTokenResponseClient appleTokenClient;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // STATELESS -> IF_REQUIRED로 변경
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                        .requestMatchers(WHITELISTED_URLS.toArray(String[]::new)).permitAll()
                        .requestMatchers(ADMIN_PATH).hasRole(ADMIN_ROLE)
                        .requestMatchers(USER_PATH).hasAnyRole(USER_ROLE)
                        .anyRequest().authenticated());

        http.oauth2Login(oauth2 -> oauth2
                .tokenEndpoint(token -> token.accessTokenResponseClient(appleTokenClient))
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(customOauth2UserService)   // Kakao 등 OAuth2
                        .oidcUserService(customOidcUserService) // Apple 등 OIDC
                )
                .successHandler(customOauth2SuccessHandler)
                .failureHandler(customOauth2FailHandler)
        );

//        http.exceptionHandling((exception) ->
//                exception.authenticationEntryPoint(customAuthenticationEntryPoint)
//                        .accessDeniedHandler(customAccessDeniedHandler));

        http.addFilterBefore(jwtExceptionFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}
