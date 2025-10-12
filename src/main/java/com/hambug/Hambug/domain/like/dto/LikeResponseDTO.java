package com.hambug.Hambug.domain.like.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponseDTO {

    private Long boardId;
    private boolean isLiked;
    private long likeCount;

    public static LikeResponseDTO of(Long boardId, boolean isLiked, long likeCount) {
        return LikeResponseDTO.builder()
                .boardId(boardId)
                .isLiked(isLiked)
                .likeCount(likeCount)
                .build();
    }
}
