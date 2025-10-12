package com.hambug.Hambug.domain.user.service.impl;

import com.hambug.Hambug.domain.auth.dto.JwtTokenDto;
import com.hambug.Hambug.domain.auth.service.JwtService;
import com.hambug.Hambug.domain.oauth.service.Oauth2UserInfo;
import com.hambug.Hambug.domain.user.dto.UserDto;
import com.hambug.Hambug.domain.user.entity.User;
import com.hambug.Hambug.domain.user.repository.UserRepository;
import com.hambug.Hambug.domain.user.service.UserService;
import com.hambug.Hambug.global.exception.ErrorCode;
import com.hambug.Hambug.global.exception.custom.AlreadyEntityException;
import com.hambug.Hambug.global.exception.custom.JwtException;
import com.hambug.Hambug.global.s3.service.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final S3Service s3Service;


    @Override
    public UserDto signUpOrLogin(Oauth2UserInfo userInfo) {
        return userRepository.findBySocialIdAndLoginType(userInfo.getId(), userInfo.getLoginType())
                .map(this::login)
                .orElseGet(() -> register(userInfo));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getById(Long userId) {
        User user = getUser(userId);
        return UserDto.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getReferenceById(Long userId) {
        return userRepository.getReferenceById(userId);
    }

    @Override
    public UserDto updateNickname(Long userId, Long authUserId, String nickname) {
        validOwner(userId, authUserId);
        userRepository.findByNickname(nickname).ifPresent(existing -> {
            throw new AlreadyEntityException(ErrorCode.ALREADY_ENTITY, "중복된 닉네임 입니다.");
        });

        User user = getUser(userId);
        user.updateNickname(nickname);
        return UserDto.toDto(user);
    }


    @SneakyThrows
    @Override
    public UserDto updateProfileImage(Long userId, Long authUserId, MultipartFile file) {
        validOwner(userId, authUserId);
        User user = getUser(userId);
        String uploadImage = s3Service.uploadImage(file);
        user.updateProfileImage(uploadImage);
        return UserDto.toDto(user);
    }

    @Override
    public void softDeleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을수 없습니다."));
        user.setActive(false);
        user.markDeleted();
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("사용자를 찾을수 없습니다."));
    }

    private void validOwner(Long userId, Long authUserId) {
        if (!userId.equals(authUserId)) {
            throw new JwtException(ErrorCode.JWT_TOKEN_INVALID, "본인만 수정 가능합니다.");
        }
    }

    private UserDto register(Oauth2UserInfo userInfo) {
        User user = userRepository.save(User.of(userInfo, generateRandomNickname()));
        UserDto userDto = UserDto.authUserDTO(user, true);
        userDto.addTokens(jwtService.generateTokens(userDto));
        jwtService.socialRefreshToken(userInfo, userDto);
        return userDto;
    }

    private UserDto login(User user) {
        UserDto userDto = UserDto.authUserDTO(user, false);
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
