package com.hambug.Hambug.domain.board.dto;


import com.hambug.Hambug.domain.board.entity.Board;
import com.hambug.Hambug.domain.board.entity.Category;

import java.time.LocalDateTime;
import java.util.List;

public class BoardResponseDTO {


    public record BoardResponse(
            Long id,
            String title,
            String content,
            Category category,
            List<String> imageUrls,
            String authorNickname,
            Long authorId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public BoardResponse(Board board) {
            this(
                    board.getId(),
                    board.getTitle(),
                    board.getContent(),
                    board.getCategory(),
                    board.getImageUrls(),
                    board.getUser().getNickname(),
                    board.getUser().getId(),
                    board.getCreatedAt(),
                    board.getModifiedAt()
            );
        }
    }
}