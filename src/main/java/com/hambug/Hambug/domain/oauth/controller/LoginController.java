package com.hambug.Hambug.domain.oauth.controller;

import com.hambug.Hambug.domain.auth.dto.JwtTokenDto;
import com.hambug.Hambug.domain.oauth.api.Oauth2Api;
import com.hambug.Hambug.domain.oauth.dto.Oauth2RequestDTO;
import com.hambug.Hambug.domain.oauth.service.Oauth2ServiceFactory;
import com.hambug.Hambug.domain.user.dto.UserDto;
import com.hambug.Hambug.global.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
@Slf4j
public class LoginController implements Oauth2Api {

    private final Oauth2ServiceFactory oauth2ServiceFactory;

    @PostMapping("/{provider}")
    public CommonResponse<JwtTokenDto> login(@PathVariable String provider, @RequestBody @Valid Oauth2RequestDTO.LoginAuthCode payload) {
        UserDto userDto = oauth2ServiceFactory.getService(provider).login(payload.authorizationCode());
        return CommonResponse.ok(userDto.toJwtTokenDto());
    }

    @GetMapping("/callback")
    public void callback() {
        log.info("여기로 접근합니가?");

    }

}
