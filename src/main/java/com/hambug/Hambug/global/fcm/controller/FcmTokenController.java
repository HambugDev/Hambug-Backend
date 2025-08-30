package com.hambug.Hambug.global.fcm.controller;

import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import com.hambug.Hambug.global.fcm.api.FcmTokenApi;
import com.hambug.Hambug.global.fcm.dto.RegisterFcmTokenRequest;
import com.hambug.Hambug.global.fcm.service.FcmDeviceTokenService;
import com.hambug.Hambug.global.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fcm")
@RequiredArgsConstructor
public class FcmTokenController implements FcmTokenApi {

    private final FcmDeviceTokenService fcmDeviceTokenService;

    @PostMapping("/tokens")
    public CommonResponse<?> register(@Valid @RequestBody RegisterFcmTokenRequest request, Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Long userId = principal.getUserDto().getUserId();
        fcmDeviceTokenService.registerOrUpdate(userId, request);
        return CommonResponse.ok(true);
    }
}
