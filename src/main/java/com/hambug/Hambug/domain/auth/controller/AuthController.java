package com.hambug.Hambug.domain.auth.controller;

import com.hambug.Hambug.domain.auth.api.AuthApi;
import com.hambug.Hambug.domain.auth.dto.AuthResponseDto;
import com.hambug.Hambug.domain.auth.dto.Oauth2RequestDTO;
import com.hambug.Hambug.domain.auth.service.JwtService;
import com.hambug.Hambug.domain.auth.service.oauth2.Oauth2ServiceFactory;
import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import com.hambug.Hambug.domain.user.dto.UserDto;
import com.hambug.Hambug.global.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private static final String BEARER = "Bearer ";
    private final JwtService jwtService;
    private final Oauth2ServiceFactory oauth2ServiceFactory;

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

    @GetMapping("/me")
    public CommonResponse<UserDto> me(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        UserDto user = principalDetails.getUser();
        return CommonResponse.ok(user);
    }

    @PostMapping("/unlink/{provider}")
    public CommonResponse<Boolean> unlink(@PathVariable String provider,
                                          @AuthenticationPrincipal PrincipalDetails principalDetails) {
        oauth2ServiceFactory.getService(provider).unlink(principalDetails.getUser().getUserId());
        return CommonResponse.ok(true);
    }

    @PostMapping("/login/{provider}")
    public CommonResponse<AuthResponseDto.LoginResponse> login(@PathVariable String provider,
                                                               @RequestBody @Valid Oauth2RequestDTO.LoginAuthCode payload) {
        oauth2ServiceFactory.validateProvider(provider, payload.accessToken());
        UserDto userDto = oauth2ServiceFactory.getService(provider).login(payload.accessToken());

        PrincipalDetails principal = new PrincipalDetails(userDto);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null && attrs.getRequest() != null) {
            attrs.getRequest().getSession(true)
                    .setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        }
        return CommonResponse.ok(new AuthResponseDto.LoginResponse(userDto, userDto.toJwtTokenDto()));
    }

    private Long getUserId(PrincipalDetails principalDetails) {
        return principalDetails.getUser().getUserId();
    }
}
