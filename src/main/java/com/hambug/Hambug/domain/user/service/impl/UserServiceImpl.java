package com.hambug.Hambug.domain.user.service.impl;

import com.hambug.Hambug.domain.jwt.dto.JwtTokenDto;
import com.hambug.Hambug.domain.jwt.service.JwtService;
import com.hambug.Hambug.domain.oauth.service.Oauth2UserInfo;
import com.hambug.Hambug.domain.user.dto.UserDto;
import com.hambug.Hambug.domain.user.entity.User;
import com.hambug.Hambug.domain.user.repository.UserRepository;
import com.hambug.Hambug.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public UserDto signUpOrLogin(Oauth2UserInfo userInfo) {
        log.info("접근은함");
        return userRepository.findByEmail(userInfo.getEmail())
                .map(user -> {
                    UserDto userDto = UserDto.authUserDTO(user, true);
                    String accessToken = jwtService.generateAccessToken(userDto);
                    String refreshToken = jwtService.getRefreshToken(userDto.getUserId());
                    userDto.addTokens(JwtTokenDto.of(accessToken, refreshToken));
                    return userDto;
                })
                .orElseGet(() -> {
                    User user = userRepository.save(User.of(userInfo));
                    UserDto userDto = UserDto.authUserDTO(user, true);
                    userDto.addTokens(jwtService.generateTokens(userDto));
                    return UserDto.authUserDTO(user, false);
                });
    }
}
