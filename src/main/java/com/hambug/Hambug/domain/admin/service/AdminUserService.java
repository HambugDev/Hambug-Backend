package com.hambug.Hambug.domain.admin.service;

import com.hambug.Hambug.domain.admin.dto.AdminUserDto;
import com.hambug.Hambug.domain.admin.dto.AdminUserReqDTO;
import com.hambug.Hambug.domain.admin.entity.AdminUser;
import com.hambug.Hambug.domain.admin.repository.AdminUserRepository;
import com.hambug.Hambug.domain.admin.util.PasswordUtil;
import com.hambug.Hambug.domain.auth.dto.JwtTokenDto;
import com.hambug.Hambug.domain.auth.service.AdminJwtPrincipalAdapter;
import com.hambug.Hambug.domain.auth.service.JwtService;
import com.hambug.Hambug.domain.user.entity.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService {

    private final AdminUserRepository userRepository;
    private final EmailVerificationService emailVerificationService;
    private final JwtService jwtService;

    public AdminUserDto getById(Long adminUserId) {
        AdminUser adminUser = userRepository.findById(adminUserId)
                .orElseThrow(() -> new EntityNotFoundException("해당 관리자를 찾을수 없습니다."));
        return AdminUserDto.of(adminUser);
    }

    public String createAdminUser(AdminUserReqDTO.RegisterAdminUser dto) {
        userRepository.findByEmail(dto.email())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + dto.email());
                });
        String tempPassword = PasswordUtil.generateTempPassword(16);
        String salt = PasswordUtil.generateSalt(16);
        String hashed = PasswordUtil.hashWithPbkdf2(tempPassword, salt);
        userRepository.save(AdminUser.admin_of(dto.name(), dto.email(), hashed, salt));

        return tempPassword;
    }

    public String createManagerUser(AdminUserReqDTO.RegisterAdminUser dto) {
        userRepository.findByEmail(dto.email())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + dto.email());
                });
        String tempPassword = PasswordUtil.generateTempPassword(16);
        String salt = PasswordUtil.generateSalt(16);
        String hashed = PasswordUtil.hashWithPbkdf2(tempPassword, salt);
        userRepository.save(AdminUser.manager_of(dto.name(), dto.email(), hashed, salt));

        return tempPassword;
    }

    public JwtTokenDto login(AdminUserReqDTO.AdminLogin body) {
        AdminUser user = userRepository.findByEmail(body.email())
                .orElseThrow(() -> new EntityNotFoundException("이메일 또는 비밀번호가 올바르지 않습니다."));
        if (user.getRole().equals(Role.ROLE_ADMIN) && !user.isEmailVerification()) {
            emailVerificationService.create(user);
            throw new RuntimeException("이메일 인증이 안되어 있습니다.");
        }
        boolean pwOk = PasswordUtil.matchesPbkdf2(body.password(), user.getSalt(), user.getPassword());
        if (!pwOk) {
            throw new EntityNotFoundException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        return jwtService.generateTokens(new AdminJwtPrincipalAdapter(user));
    }
}
