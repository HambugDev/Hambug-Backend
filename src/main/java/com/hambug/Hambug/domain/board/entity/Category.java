package com.hambug.Hambug.domain.board.entity;

import lombok.Getter;

@Getter
public enum Category {
    FREE_TALK("자유잡담"),
    REVIEW("햄버거 리뷰"),
    RECOMMENDATION("맛집추천");

    private final String description;

    Category(String description) {
        this.description = description;
    }
}
