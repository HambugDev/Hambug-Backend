package com.hambug.Hambug.domain.board.entity;

import lombok.Getter;

@Getter
public enum Category {
    FREE_TALK("자유잡담"),
    FRANCHISE("프랜차이즈"),
    HANDMADE("수제버거"),
    RECOMMENDATION("맛집추천");

    private final String description;

    Category(String description) {
        this.description = description;
    }
}
