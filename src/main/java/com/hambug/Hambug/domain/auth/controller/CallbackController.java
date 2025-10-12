package com.hambug.Hambug.domain.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/login/oauth2/code")
public class CallbackController {

    @GetMapping("/kakao")
    public void kakaoCallback(@RequestParam("code") String code) {
        log.info("💬 Kakao callback code = {}", code);
    }

    @PostMapping("/apple")
    public void appleCallback(@RequestParam Map<String, String> body) {
        log.info(" Apple callback received: {}", body);
        String code = body.get("code");
        String idToken = body.get("id_token");
        String state = body.get("state");
        String user = body.get("user"); // JSON 문자열로 들어옴

        log.info("code: {}", code);
        log.info("idToken: {}", idToken);
        log.info("state: {}", state);
        log.info("user: {}", user);
    }
}
