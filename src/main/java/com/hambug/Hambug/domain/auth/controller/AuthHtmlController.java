package com.hambug.Hambug.domain.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AuthHtmlController {

    @GetMapping(value = "/api/auth/apple/android/callback", produces = "text/html")
    @ResponseBody
    public String appleCallback() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
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
