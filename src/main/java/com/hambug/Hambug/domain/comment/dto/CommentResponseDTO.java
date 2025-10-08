package com.hambug.Hambug.domain.comment.dto;

import com.hambug.Hambug.domain.comment.entity.Comment;
import java.time.LocalDateTime;

public class CommentResponseDTO {

    public record CommentResponse(
            Long id,
            String content,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public CommentResponse(Comment comment) {
            this(
                    comment.getId(),
                    comment.getContent(),
                    comment.getCreatedAt(),
                    comment.getModifiedAt()
            );
        }
    }
}
