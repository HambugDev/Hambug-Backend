package com.hambug.Hambug.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public class UserRequestDto {

    public record UpdateUserNicknameReqDto(
            @NotBlank(message = "닉네임은 필수로 입력해 주세요")
            String nickname) {
    }

}
