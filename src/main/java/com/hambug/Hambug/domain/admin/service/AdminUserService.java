package com.hambug.Hambug.domain.admin.service;

import com.hambug.Hambug.domain.admin.dto.AdminUserDto;
import com.hambug.Hambug.domain.admin.dto.AdminUserResponseDto;
import com.hambug.Hambug.domain.admin.entity.AdminUser;
import com.hambug.Hambug.domain.admin.entity.EmailVerification;
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

import java.util.Optional;

import static com.hambug.Hambug.domain.admin.dto.AdminUserReqDTO.*;

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

    public AdminUserResponseDto.AdminUserResponse createAdminUser(RegisterAdminUser dto) {
        userRepository.findByEmail(dto.email())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + dto.email());
                });
        String tempPassword = PasswordUtil.generateTempPassword(16);
        String salt = PasswordUtil.generateSalt(16);
        String hashed = PasswordUtil.hashWithPbkdf2(tempPassword, salt);
        AdminUser adminUser = userRepository.save(AdminUser.admin_of(dto.name(), dto.email(), hashed, salt));

        return new AdminUserResponseDto.AdminUserResponse(adminUser, tempPassword);
    }

    public AdminUserResponseDto.AdminUserResponse createManagerUser(RegisterAdminUser dto) {
        userRepository.findByEmail(dto.email())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + dto.email());
                });
        String tempPassword = PasswordUtil.generateTempPassword(16);
        String salt = PasswordUtil.generateSalt(16);
        String hashed = PasswordUtil.hashWithPbkdf2(tempPassword, salt);
        AdminUser adminUser = userRepository.save(AdminUser.manager_of(dto.name(), dto.email(), hashed, salt));

        return new AdminUserResponseDto.AdminUserResponse(adminUser, tempPassword);
    }

    public AdminUserResponseDto.AdminLoginResponse login(AdminLogin body) {
        AdminUser user = userRepository.findByEmail(body.email())
                .orElseThrow(() -> new EntityNotFoundException("이메일 또는 비밀번호가 올바르지 않습니다."));
        if (user.getRole().equals(Role.ROLE_ADMIN) && !user.isEmailVerification()) {
//            emailVerificationService.create(user);
            throw new RuntimeException("이메일 인증이 안되어 있습니다.");
        }
        boolean pwOk = PasswordUtil.matchesPbkdf2(body.password(), user.getSalt(), user.getPassword());
        if (!pwOk) {
            throw new EntityNotFoundException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        JwtTokenDto tokens = jwtService.generateTokens(new AdminJwtPrincipalAdapter(user));
        return AdminUserResponseDto.AdminLoginResponse.of(user, tokens);
    }

    public boolean resetPassword(ResetPassword body) {
        AdminUser user = userRepository.findByEmail(body.email())
                .orElseThrow(() -> new EntityNotFoundException("관리자 계정을 찾을 수 없습니다."));

        if (Boolean.TRUE.equals(user.getIsTempPassword())) {
            throw new IllegalStateException("임시 비밀번호 상태에서는 비밀번호 재설정을 사용할 수 없습니다.");
        }
        if (user.getRole().equals(Role.ROLE_ADMIN) && !user.isEmailVerification()) {
            throw new IllegalArgumentException("이메일 인증하기 전에는 비밀번호 재설정을 사용할 수 없습니다.");
        }

        PasswordUtil.matchesPbkdf2(body.oldPassword(), user.getSalt(), user.getPassword());

        String newSalt = PasswordUtil.generateSalt(16);
        String newHash = PasswordUtil.hashWithPbkdf2(body.newPassword(), newSalt);
        user.setSalt(newSalt);
        user.setPassword(newHash);
        user.setIsTempPassword(true);
        return true;
    }

    public AdminUserResponseDto.AdminValidSendEmailResponse sendEmail(AdminValidSendEmail body) {
        Optional<AdminUser> byEmail = userRepository.findByEmail(body.email());
        if (byEmail.isEmpty()) {
            throw new EntityNotFoundException("해당 어드민 계정을 찾을수 없습니다.");
        }
        AdminUser adminUser = byEmail.get();
        if (!adminUser.getRole().equals(Role.ROLE_ADMIN)) {
            throw new EntityNotFoundException("해당 어드민 계정을 찾을수 없습니다.");
        }
        EmailVerification emailVerification = emailVerificationService.create(adminUser);
        return new AdminUserResponseDto.AdminValidSendEmailResponse(emailVerification.getId(), emailVerification.getExpiredAt());
    }

    public void verificationEmail(VerificationEmail body) {
        EmailVerification emailVerification = emailVerificationService.findById(body.emailVerificationId());

        // 이미 인증된 경우는 아이덴포턴트하게 통과
        if (emailVerification.isVerified()) {
            return;
        }

        // 만료 여부 검사
        if (emailVerification.isExpired()) {
            throw new IllegalArgumentException("인증코드가 만료되었습니다.");
        }

        // 코드 일치 검사
        if (!emailVerification.isVerified(body.verificationCode())) {
            throw new IllegalArgumentException("해당 인증코드는 잘못된 코드입니다.");
        }

        // 인증 완료 상태 반영
        emailVerification.markVerified();
    }
}
