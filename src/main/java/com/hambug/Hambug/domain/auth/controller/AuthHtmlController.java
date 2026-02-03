package com.hambug.Hambug.domain.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AuthHtmlController {

        @GetMapping(
                value = "/api/auth/apple/android/callback",
                produces = "text/html; charset=UTF-8"
        )
        @ResponseBody
        public String appleAndroidCallback() {
            return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>로그인 처리 중입니다</title>
            </head>
            <body>
                <script>
                    const fragment = window.location.hash;
                    window.location.href = 'hambug://apple-callback' + fragment;
                </script>
            </body>
            </html>
            """;
        }
}
