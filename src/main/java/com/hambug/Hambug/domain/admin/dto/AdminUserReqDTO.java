package com.hambug.Hambug.domain.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AdminUserReqDTO {

    public record RegisterAdminUser(
            @NotBlank(message = "이메일은 필수 입니다.")
            String email,

            @NotBlank(message = "이름은 필수 입니다.")
            String name) {
    }

    public record AdminLogin(
            @NotBlank(message = "이메일은 필수 입니다.")
            String email,

            @NotBlank(message = "비밀번호는 필수 입니다.")
            String password) {
    }

    public record ResetPassword(
            @NotBlank(message = "이메일은 필수 입니다.")
            String email,
            @NotBlank(message = "기존 비밀번호는 필수 입니다.")
            String oldPassword,
            @NotBlank(message = "새 비밀번호는 필수 입니다.")
            String newPassword) {
    }

    public record AdminValidSendEmail(
            @NotBlank(message = "이메일은 필수 입니다.")
            String email
    ) {
    }

    public record VerificationEmail(
            @NotNull(message = "검증아이디를 입력해주세요")
            Long emailVerificationId,

            @NotBlank(message = "인증 코드를 입력해주세요")
            String verificationCode
    ) {
    }
}
