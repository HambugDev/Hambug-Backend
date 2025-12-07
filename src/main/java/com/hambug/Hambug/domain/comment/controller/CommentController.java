package com.hambug.Hambug.domain.comment.controller;

import com.hambug.Hambug.domain.comment.api.CommentApi;
import com.hambug.Hambug.domain.comment.dto.CommentRequestDTO;
import com.hambug.Hambug.domain.comment.dto.CommentResponseDTO;
import com.hambug.Hambug.domain.comment.service.CommentService;
import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import com.hambug.Hambug.global.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards/{boardId}/comments")
public class CommentController implements CommentApi {

    private final CommentService commentService;

    private static Long getUserId(PrincipalDetails principalDetails) {
        return principalDetails.getUser().getUserId();
    }

    @Override
    @GetMapping
    public CommonResponse<CommentResponseDTO.CommentAllResponse> getComments(@PathVariable("boardId") Long boardId,
                                                                             @RequestParam(required = false) Long lastId,
                                                                             @RequestParam(defaultValue = "10") int limit,
                                                                             @RequestParam(defaultValue = "DESC") String order) {
        CommentResponseDTO.CommentAllResponse comments = commentService.findCommentsByBoard(boardId, lastId, limit, order.toLowerCase());
        return CommonResponse.ok(comments);
    }

    @Override
    @PostMapping
    public CommonResponse<CommentResponseDTO.CommentResponse> createComment(
            @PathVariable("boardId") Long boardId,
            @RequestBody CommentRequestDTO.CommentCreateRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Long userId = getUserId(principalDetails);
        CommentResponseDTO.CommentResponse comment = commentService.createComment(boardId, userId, request);
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