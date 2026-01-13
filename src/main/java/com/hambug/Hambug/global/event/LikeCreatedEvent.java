package com.hambug.Hambug.global.event;

public record LikeCreatedEvent(
        Long boardAuthorId,
        Long likeUserId,
        Long boardId,
        String likeAuthorNickname
) {
}
