package com.hambug.Hambug.domain.auth.dto;

import com.hambug.Hambug.domain.user.dto.UserDto;

public class AuthResponseDto {

    public record LoginResponse(UserDto user, JwtTokenDto token) {
    }
}
