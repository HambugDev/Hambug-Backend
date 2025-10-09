package com.hambug.Hambug.domain.board.dto;

import com.hambug.Hambug.domain.board.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class BoardRequestDTO {

    public record BoardCreateRequest(
            @NotBlank(message = "제목은 필수 입력 값입니다.")
            String title,

            @NotBlank(message = "내용은 필수 입력 값입니다.")
            String content,

            @NotNull(message = "카테고리는 필수 선택 값입니다.")
            Category category,

            List<String> imageUrls
    ) {}

    public record BoardUpdateRequest(
            String title,
            String content,
            Category category,
            List<String> imageUrls
    ) {}
}