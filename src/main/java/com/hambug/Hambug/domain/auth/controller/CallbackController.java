package com.hambug.Hambug.domain.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("/login/oauth2/code")
public class CallbackController {

    @GetMapping("/apple")
    public void appleCallback() {
        log.info("apple callback");
    }
}
