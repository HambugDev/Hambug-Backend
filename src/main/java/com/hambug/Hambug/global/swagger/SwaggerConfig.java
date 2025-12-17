package com.hambug.Hambug.global.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Server httpsServer = new Server()
                .url("https://hambug.p-e.kr")
                .description("Hambug Production API (HTTPS)");

        SecurityScheme accessTokenScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("Access Token을 입력하세요. (예: `eyJhbGciOi...`)");

//        SecurityScheme refreshTokenScheme = new SecurityScheme()
//                .type(SecurityScheme.Type.APIKEY)
//                .in(SecurityScheme.In.HEADER)
//                .name("RefreshToken")
//                .description("Refresh Token을 입력하세요. (선택)");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Authorization")
                .addList("RefreshToken");

        return new OpenAPI()
                .info(new Info()
                        .title("Hambug API 명세서")
                        .description("Hambug 서비스의 REST API 문서입니다.")
                        .version("v1"))
                .servers(List.of(httpsServer))
                .components(new Components()
                        .addSecuritySchemes("Authorization", accessTokenScheme))
                .addSecurityItem(securityRequirement);
    }
}
