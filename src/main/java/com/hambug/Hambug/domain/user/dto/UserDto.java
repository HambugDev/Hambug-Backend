package com.hambug.Hambug.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hambug.Hambug.domain.auth.dto.JwtTokenDto;
import com.hambug.Hambug.domain.user.entity.Role;
import com.hambug.Hambug.domain.user.entity.User;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    Long userId;

    String email;

    String name;

    String nickname;

    String profileImageUrl;

    Role role;

    @JsonIgnore
    boolean isLogin;

    @JsonIgnore
    boolean isActive;

    @JsonIgnore
    String accessToken;

    @JsonIgnore
    String refreshToken;

    public static UserDto authUserDTO(User user) {
        return UserDto.builder()
                .userId(user.getId())
                .name(user.getName())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.isActive()).build();
    }

    public static UserDto reissue(Claims claims) {
        return UserDto.builder()
                .userId(Long.parseLong(claims.getSubject()))
                .nickname(claims.get("nickname").toString())
                .role(Role.valueOf(claims.get("role").toString()))
                .build();
    }

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .userId(user.getId())
                .name(user.getName())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.isActive()).build();
    }

    public void addTokens(JwtTokenDto jwtTokenDto) {
        this.accessToken = jwtTokenDto.getAccessToken();
        this.refreshToken = jwtTokenDto.getRefreshToken();
    }

    public String userIdStr() {
        return String.valueOf(this.userId);
    }

}
