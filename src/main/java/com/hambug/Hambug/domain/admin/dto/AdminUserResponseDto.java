package com.hambug.Hambug.domain.admin.dto;

import com.hambug.Hambug.domain.admin.entity.AdminUser;
import com.hambug.Hambug.domain.auth.dto.JwtTokenDto;

import java.time.LocalDateTime;

public class AdminUserResponseDto {
    public record AdminUserResponse(Long id, String email, String name, String role, boolean isTempPassword, String tempPassword) {

        public AdminUserResponse(AdminUser adminUser, String tempPassword) {
            this(
                    adminUser.getId(),
                    adminUser.getEmail(),
                    adminUser.getName(),
                    adminUser.getRole().name(),
                    false,
                    tempPassword
            );
        }
    }

    public record AdminLoginResponse(Long id, String email, String name, String role, boolean isTempPassword, String accessToken,
                                     String refreshToken) {
        public static AdminLoginResponse of(AdminUser adminUser, JwtTokenDto tokens) {
            return new AdminLoginResponse(
                    adminUser.getId(),
                    adminUser.getEmail(),
                    adminUser.getName(),
                    adminUser.getRole().name(),
                    adminUser.getIsTempPassword(),
                    tokens != null ? tokens.getAccessToken() : null,
                    tokens != null ? tokens.getRefreshToken() : null
            );
        }
    }

    public record AdminValidSendEmailResponse(Long verificationId, LocalDateTime expiredAt) {
        public AdminValidSendEmailResponse(Long verificationId, LocalDateTime expiredAt) {
            this.verificationId = verificationId;
            this.expiredAt = expiredAt;
        }

    }
}
