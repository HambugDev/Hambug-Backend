package com.hambug.Hambug.domain.comment.controller;

import com.hambug.Hambug.domain.comment.api.CommentApi;
import com.hambug.Hambug.domain.comment.dto.CommentRequestDTO;
import com.hambug.Hambug.domain.comment.dto.CommentResponseDTO;
import com.hambug.Hambug.domain.comment.service.CommentService;
import com.hambug.Hambug.global.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards/{boardId}/comments")
public class CommentController implements CommentApi {

    private final CommentService commentService;

    @Override
    @GetMapping
    public CommonResponse<List<CommentResponseDTO.CommentResponse>> getComments(@PathVariable("boardId") Long boardId) {
        List<CommentResponseDTO.CommentResponse> comments = commentService.findCommentsByBoard(boardId);
        return CommonResponse.ok(comments);
    }

    @Override
    @PostMapping
    public CommonResponse<CommentResponseDTO.CommentResponse> createComment(
            @PathVariable("boardId") Long boardId,
            @RequestBody CommentRequestDTO.CommentCreateRequest request) {
        CommentResponseDTO.CommentResponse comment = commentService.createComment(boardId, request);
        return CommonResponse.ok(comment);
    }

    @Override
    @PutMapping("/{commentId}")
    public CommonResponse<CommentResponseDTO.CommentResponse> updateComment(
            @PathVariable("boardId") Long boardId,
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentRequestDTO.CommentUpdateRequest request) {
        CommentResponseDTO.CommentResponse updatedComment = commentService.updateComment(boardId, commentId, request);
        return CommonResponse.ok(updatedComment);
    }

    @Override
    @DeleteMapping("/{commentId}")
    public CommonResponse<Boolean> deleteComment(
            @PathVariable("boardId") Long boardId,
            @PathVariable("commentId") Long commentId) {
        commentService.deleteComment(boardId, commentId);
        return CommonResponse.ok(true);
    }
}