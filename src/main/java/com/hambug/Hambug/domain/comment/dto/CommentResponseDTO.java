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
            Boolean isAuthor,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public CommentResponse(Comment comment) {
            this(
                    comment.getId(),
                    comment.getContent(),
                    getAuthorIdSafely(comment),
                    getNicknameSafely(comment),
                    getProfileImageUrlSafely(comment),
                    false,
                    comment.getCreatedAt(),
                    comment.getModifiedAt()
            );
        }

        public CommentResponse(Comment comment, Boolean isAuthor) {
            this(
                    comment.getId(),
                    comment.getContent(),
                    getAuthorIdSafely(comment),
                    getNicknameSafely(comment),
                    getProfileImageUrlSafely(comment),
                    isAuthor,
                    comment.getCreatedAt(),
                    comment.getModifiedAt()
            );
        }

        private static String getNicknameSafely(Comment comment) {
            try {
                return comment.getUser() != null ? comment.getUser().getNickname() : "알 수 없는 사용자";
            } catch (jakarta.persistence.EntityNotFoundException e) {
                return "알 수 없는 사용자";
            }
        }

        private static String getProfileImageUrlSafely(Comment comment) {
            try {
                return comment.getUser() != null ? comment.getUser().getProfileImageUrl() : null;
            } catch (jakarta.persistence.EntityNotFoundException e) {
                return null;
            }
        }

        private static Long getAuthorIdSafely(Comment comment) {
            try {
                return comment.getUser() != null ? comment.getUser().getId() : null;
            } catch (jakarta.persistence.EntityNotFoundException e) {
                return null;
            }
        }
    }
}
