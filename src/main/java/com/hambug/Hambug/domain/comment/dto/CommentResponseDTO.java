package com.hambug.Hambug.domain.comment.dto;

import com.hambug.Hambug.domain.comment.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponseDTO {

    public record CommentAllResponse(List<CommentResponse> content, Long netCursorId,
                                     Boolean nextPage) {
    }

    public record CommentResponse(
            Long id,
            String content,
            Long authorId,
            String authorNickname,
            String authorProfileImageUrl,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public CommentResponse(Comment comment) {
            this(
                    comment.getId(),
                    comment.getContent(),
                    comment.getUser().getId(),
                    comment.getUser().getNickname(),
                    comment.getUser().getProfileImageUrl(),
                    comment.getCreatedAt(),
                    comment.getModifiedAt()
            );
        }
    }
}
