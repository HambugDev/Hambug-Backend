package com.hambug.Hambug.domain.admin.dto;

import jakarta.validation.constraints.NotBlank;

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

}
