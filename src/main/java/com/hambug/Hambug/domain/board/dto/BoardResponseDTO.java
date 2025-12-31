package com.hambug.Hambug.domain.board.dto;


import com.hambug.Hambug.domain.board.entity.Board;
import com.hambug.Hambug.domain.board.entity.Category;

import java.time.LocalDateTime;
import java.util.List;

public class BoardResponseDTO {

    public record BoardAllResponse(
            List<BoardResponse> content,
            Long netCursorId,
            Boolean nextPage
    ) {
    }


    public record BoardResponse(
            Long id,
            String title,
            String content,
            Category category,
            List<String> imageUrls,
            String authorNickname,
            Long authorId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Long viewCount,
            Long likeCount,
            Long commentCount,
            Boolean isLiked
    ) {
        public BoardResponse(Board board) {
            this(
                    board.getId(),
                    board.getTitle(),
                    board.getContent(),
                    board.getCategory(),
                    board.getImageUrls(),
                    getNicknameSafely(board),
                    getAuthorIdSafely(board),
                    board.getCreatedAt(),
                    board.getModifiedAt(),
                    board.getViewCount(),
                    0L,
                    board.getCommentCount(),
                    false
            );
        }

        public BoardResponse(Board board, Long likeCount, Boolean isLiked) {
            this(
                    board.getId(),
                    board.getTitle(),
                    board.getContent(),
                    board.getCategory(),
                    board.getImageUrls(),
                    getNicknameSafely(board),
                    getAuthorIdSafely(board),
                    board.getCreatedAt(),
                    board.getModifiedAt(),
                    board.getViewCount(),
                    likeCount,
                    board.getCommentCount(),
                    isLiked
            );
        }

        private static String getNicknameSafely(Board board) {
            try {
                return board.getUser() != null ? board.getUser().getNickname() : "알 수 없는 사용자";
            } catch (jakarta.persistence.EntityNotFoundException e) {
                return "알 수 없는 사용자";
            }
        }

        private static Long getAuthorIdSafely(Board board) {
            try {
                return board.getUser() != null ? board.getUser().getId() : null;
            } catch (jakarta.persistence.EntityNotFoundException e) {
                return null;
            }
        }
    }

    public record BoardDetailResponse(
            Long id,
            String title,
            String content,
            Category category,
            List<String> imageUrls,
            String authorNickname,
            String authorProfileImageUrl,
            Long authorId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Long viewCount,
            Long likeCount,
            Long commentCount,
            Boolean isLiked,
            Boolean isAuthor
    ) {
        public BoardDetailResponse(Board board, Long likeCount, Boolean isLiked, Boolean isAuthor) {
            this(
                    board.getId(),
                    board.getTitle(),
                    board.getContent(),
                    board.getCategory(),
                    board.getImageUrls(),
                    getNicknameSafely(board),
                    getProfileImageUrlSafely(board),
                    getAuthorIdSafely(board),
                    board.getCreatedAt(),
                    board.getModifiedAt(),
                    board.getViewCount(),
                    likeCount,
                    board.getCommentCount(),
                    isLiked,
                    isAuthor
            );
        }

        private static String getNicknameSafely(Board board) {
            try {
                return board.getUser() != null ? board.getUser().getNickname() : "알 수 없는 사용자";
            } catch (jakarta.persistence.EntityNotFoundException e) {
                return "알 수 없는 사용자";
            }
        }

        private static String getProfileImageUrlSafely(Board board) {
            try {
                return board.getUser() != null ? board.getUser().getProfileImageUrl() : null;
            } catch (jakarta.persistence.EntityNotFoundException e) {
                return null;
            }
        }

        private static Long getAuthorIdSafely(Board board) {
            try {
                return board.getUser() != null ? board.getUser().getId() : null;
            } catch (jakarta.persistence.EntityNotFoundException e) {
                return null;
            }
        }
    }
}
