package com.hambug.Hambug.domain.mypage.dto;

public class MyPageRequestDto {

    public record MyBoardRequest(
            Long lastId,
            Integer limit,
            String order
    ) {
        public MyBoardRequest {
            if (limit == null) {
                limit = 10;
            }
            if (order == null || order.isBlank()) {
                order = "desc";
            } else {
                order = order.toLowerCase();
            }
        }
    }

    public record MyCommentRequest(
            Long lastId,
            Integer limit,
            String order
    ) {
        public MyCommentRequest {
            if (limit == null) {
                limit = 10;
            }
            if (order == null || order.isBlank()) {
                order = "desc";
            } else {
                order = order.toLowerCase();
            }
        }
    }
}
