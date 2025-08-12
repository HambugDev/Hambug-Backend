package com.hambug.Hambug.domain.board.dto;


import com.hambug.Hambug.domain.board.entity.Board;

import java.time.LocalDateTime;

public class BoardResponseDTO {


    public record BoardResponse(
            Long id,
            String title,
            String content,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public BoardResponse(Board board) {
            this(
                    board.getId(),
                    board.getTitle(),
                    board.getContent(),
                    board.getCreatedAt(),
                    board.getModifiedAt()
            );
        }
    }
}