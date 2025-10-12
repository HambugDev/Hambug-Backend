package com.hambug.Hambug.domain.oauth.api;

import com.hambug.Hambug.domain.auth.dto.JwtTokenDto;
import com.hambug.Hambug.domain.auth.dto.Oauth2RequestDTO;
import com.hambug.Hambug.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "소셜로그인 API", description = "애플,카카오 소셜 로그인 API")
public interface Oauth2Api {

    @Operation(summary = "소셜 로그인", description = "provider(kakao|apple)와 인가코드로 로그인")
    CommonResponse<JwtTokenDto> login(String provider, Oauth2RequestDTO.LoginAuthCode payload);
}
