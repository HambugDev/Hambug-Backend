package com.hambug.Hambug.domain.burger.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BurgerRequestDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BurgerCreateRequest {
        private String menuImage;
        private String franchise;
        private String menuName;
        private String menuDescription;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BurgerUpdateRequest {
        private String menuImage;
        private String franchise;
        private String menuName;
        private String menuDescription;
    }
}
