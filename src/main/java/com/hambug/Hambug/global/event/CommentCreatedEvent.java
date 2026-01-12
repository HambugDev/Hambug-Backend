package com.hambug.Hambug.global.event;

public record CommentCreatedEvent(
        Long boardId,
        Long boardAuthorId,
        String commentAuthorName,
        String commentContent
) {
}
