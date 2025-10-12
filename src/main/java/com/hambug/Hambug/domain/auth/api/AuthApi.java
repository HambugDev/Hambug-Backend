package com.hambug.Hambug.domain.auth.api;

import com.hambug.Hambug.domain.auth.dto.AuthResponseDto;
import com.hambug.Hambug.domain.auth.dto.Oauth2RequestDTO;
import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import com.hambug.Hambug.domain.user.dto.UserDto;
import com.hambug.Hambug.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "인증/인가 API", description = "JWT 토큰 재발급 및 로그아웃 관련 API")
public interface AuthApi {

    @Operation(summary = "리프레시 토큰으로 액세스 토큰 재발급", description = "Authorization 헤더에 포함된 Bearer 리프레시 토큰으로 새 액세스 토큰을 발급합니다.")
    CommonResponse<String> refreshToken(@RequestHeader(value = "Authorization", required = false) String authorization);

    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자의 리프레시 토큰을 삭제하여 로그아웃합니다.")
    CommonResponse<Boolean> logout(@AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(
            summary = "JWT 토큰으로 내 정보 조회",
            description = "요청의 Authorization 헤더에 포함된 Bearer 액세스 토큰을 검증하여 현재 로그인한 사용자의 정보를 반환합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    CommonResponse<UserDto> me(@AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(
            summary = "회원 탈퇴",
            description = "소셜로그인 회원 탈퇴"
    )
    CommonResponse<Boolean> unlink(String provider
            , @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "소셜 로그인", description = "provider(kakao|apple)와 인가코드로 로그인")
    CommonResponse<AuthResponseDto.LoginResponse> login(String provider, Oauth2RequestDTO.LoginAuthCode payload);
}
