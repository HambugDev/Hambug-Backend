package com.hambug.Hambug.domain.user.service.impl;

import com.hambug.Hambug.domain.oauth.service.Oauth2UserInfo;
import com.hambug.Hambug.domain.token.dto.JwtTokenDto;
import com.hambug.Hambug.domain.token.service.JwtService;
import com.hambug.Hambug.domain.user.dto.UserDto;
import com.hambug.Hambug.domain.user.entity.User;
import com.hambug.Hambug.domain.user.repository.UserRepository;
import com.hambug.Hambug.domain.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private static final String NICKNAME_PREFIX = "HAMBUG_";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final long TEN_POW_10 = 1_000_000_0000L;

    private final UserRepository userRepository;
    private final JwtService jwtService;


    @Override
    public UserDto signUpOrLogin(Oauth2UserInfo userInfo) {
        return userRepository.findByEmail(userInfo.getEmail())
                .map(this::login)
                .orElseGet(() -> register(userInfo));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("사용자를 찾을수 없습니다."));
        return UserDto.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getReferenceById(Long userId) {
        return userRepository.getReferenceById(userId);
    }

    private UserDto register(Oauth2UserInfo userInfo) {
        User user = userRepository.save(User.of(userInfo, generateRandomNickname()));
        UserDto userDto = UserDto.authUserDTO(user);
        userDto.addTokens(jwtService.generateTokens(userDto));
        return userDto;
    }

    private UserDto login(User user) {
        UserDto userDto = UserDto.authUserDTO(user);
        String accessToken = jwtService.generateAccessToken(userDto);
        String refreshToken = jwtService.getRefreshToken(userDto);
        userDto.addTokens(JwtTokenDto.of(accessToken, refreshToken));
        return userDto;
    }

    private String generateRandomNickname() {
        long number = Math.floorMod(SECURE_RANDOM.nextLong(), TEN_POW_10);
        String suffix = String.format("%010d", number);
        return NICKNAME_PREFIX + suffix;
    }
}
