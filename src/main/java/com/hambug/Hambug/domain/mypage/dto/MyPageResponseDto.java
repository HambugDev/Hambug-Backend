package com.hambug.Hambug.domain.mypage.dto;

import com.hambug.Hambug.domain.board.entity.Board;
import com.hambug.Hambug.domain.board.entity.Category;
import lombok.Builder;

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
            Category category,
            List<String> imageUrls
    ) {
        public static MyBoardResponse from(Board board) {
            return MyBoardResponse.builder()
                    .id(board.getId())
                    .title(board.getTitle())
                    .content(board.getContent())
                    .category(board.getCategory())
                    .imageUrls(board.getImageUrls() != null ? board.getImageUrls() : List.of())
                    .build();
        }
    }

}
