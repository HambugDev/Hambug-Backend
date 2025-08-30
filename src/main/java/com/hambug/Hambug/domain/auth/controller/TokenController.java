package com.hambug.Hambug.domain.auth.controller;

import com.hambug.Hambug.domain.auth.api.TokenApi;
import com.hambug.Hambug.domain.auth.service.JwtService;
import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import com.hambug.Hambug.global.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class TokenController implements TokenApi {

    private static final String BEARER = "Bearer ";
    private final JwtService jwtService;

    @PostMapping("/refresh")
    public CommonResponse<String> refreshToken(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        if (authorization == null || !authorization.startsWith(BEARER)) {
            return CommonResponse.fail("Authorization header missing or invalid");
        }
        String refreshToken = authorization.substring(BEARER.length());
        String reissueTokensFromRefresh = jwtService.reissueTokensFromRefresh(refreshToken);
        return CommonResponse.ok(reissueTokensFromRefresh);
    }

    @PostMapping("/logout")
    public CommonResponse<Boolean> logout(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long userId = getUserId(principalDetails);
        jwtService.logout(userId);
        return CommonResponse.ok(true);
    }

    private Long getUserId(PrincipalDetails principalDetails) {
        return principalDetails.getUser().getUserId();
    }
}
