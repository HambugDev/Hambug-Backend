package com.hambug.Hambug.domain.burger.dto;

import com.hambug.Hambug.domain.burger.entity.Burger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

public class BurgerResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BurgerInfo {
        private Long id;
        private String menuImage;
        private String franchise;
        private String menuName;
        private String menuDescription;

        public static BurgerInfo from(Burger burger) {
            return BurgerInfo.builder()
                    .id(burger.getId())
                    .menuImage(burger.getMenuImage())
                    .franchise(burger.getFranchise())
                    .menuName(burger.getMenuName())
                    .menuDescription(burger.getMenuDescription())
                    .build();
        }

        public static List<BurgerInfo> fromList(List<Burger> burgers) {
            return burgers.stream()
                    .map(BurgerInfo::from)
                    .collect(Collectors.toList());
        }
    }
}
