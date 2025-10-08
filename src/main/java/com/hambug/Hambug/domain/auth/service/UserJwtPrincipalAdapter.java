package com.hambug.Hambug.domain.auth.service;

import com.hambug.Hambug.domain.user.dto.UserDto;

public class UserJwtPrincipalAdapter implements JwtPrincipal {

    private final UserDto userDto;

    public UserJwtPrincipalAdapter(UserDto userDto) {
        this.userDto = userDto;
    }

    public UserDto getUserDto() {
        return userDto;
    }

    @Override
    public Long getId() {
        return userDto.getUserId();
    }

    @Override
    public String getRoleAsString() {
        return userDto.getRole() != null ? userDto.getRole().name() : null;
    }

    @Override
    public String getNickname() {
        return userDto.getNickname();
    }

    @Override
    public String getPlatform() {
        return userDto.getLoginType() != null ? userDto.getLoginType().name() : null;
    }
}
