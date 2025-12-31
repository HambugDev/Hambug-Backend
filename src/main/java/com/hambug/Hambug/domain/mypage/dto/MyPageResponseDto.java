package com.hambug.Hambug.domain.mypage.dto;

import com.hambug.Hambug.domain.board.entity.Board;
import com.hambug.Hambug.domain.board.entity.Category;
import com.hambug.Hambug.domain.comment.entity.Comment;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class MyPageResponseDto {

    @Builder
    public record BoardPage(
            Boolean nextPage,
            Long nextCursorId,
            List<MyBoardResponse> content
    ) {
        public static BoardPage from(List<MyBoardResponse> content, boolean hasNext) {
            Long nextCursorId = (!content.isEmpty() && hasNext)
                    ? content.get(content.size() - 1).id()
                    : null;

            return new BoardPage(hasNext, nextCursorId, content);
        }
    }

    @Builder
    public record MyBoardResponse(
            Long id,
            String title,
            String content,
            Integer viewCount,
            Integer commentCount,
            Integer likeCount,
            Category category,
            List<String> imageUrls,
            LocalDateTime createAt
    ) {
        public static MyBoardResponse from(Board board, Long likeCount) {
            return MyBoardResponse.builder()
                    .id(board.getId())
                    .title(board.getTitle())
                    .content(board.getContent())
                    .viewCount(board.getViewCount() != null ? board.getViewCount().intValue() : 0)
                    .commentCount(board.getCommentCount() != null ? board.getCommentCount().intValue() : 0)
                    .likeCount(likeCount != null ? likeCount.intValue() : 0)
                    .category(board.getCategory())
                    .imageUrls(board.getImageUrls() != null ? board.getImageUrls() : List.of())
                    .createAt(board.getCreatedAt())
                    .build();
        }
    }

    public record CommentPage(
            Boolean nextPage,
            Long nextCursorId,
            List<MyCommentResponse> content
    ) {
        public static CommentPage from(List<MyCommentResponse> content, boolean hasNext) {
            Long nextCursorId = (!content.isEmpty() && hasNext)
                    ? content.get(content.size() - 1).commentId()
                    : null;
            return new CommentPage(hasNext, nextCursorId, content);
        }
    }

    @Builder
    public record MyCommentResponse(
            Long boardId,
            String title,
            Long commentId,
            String content,
            LocalDateTime createdAt
    ) {

        public static MyCommentResponse from(Comment comment) {
            return MyCommentResponse.builder()
                    .boardId(comment.getBoard().getId())
                    .title(comment.getBoard().getTitle())
                    .commentId(comment.getId())
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .build();
        }
    }
}
