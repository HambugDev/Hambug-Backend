package com.hambug.Hambug.domain.comment.dto;

public class CommentRequestDTO {

    public record CommentCreateRequest(String content) {
    }

    public record CommentUpdateRequest(String content) {
    }
}