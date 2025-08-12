package com.hambug.Hambug.domain.board.dto;

import jakarta.validation.constraints.NotBlank;

public class BoardRequestDTO {

    public record BoardCreateRequest(
            @NotBlank(message = "제목은 필수 입력 값입니다.")
            String title,

            @NotBlank(message = "내용은 필수 입력 값입니다.")
            String content
    ) {}

    public record BoardUpdateRequest(
            String title,
            String content
    ) {}
}