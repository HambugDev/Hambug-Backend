package com.hambug.Hambug.domain.comment.api;

import com.hambug.Hambug.domain.comment.dto.CommentRequestDTO;
import com.hambug.Hambug.domain.comment.dto.CommentResponseDTO;
import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import com.hambug.Hambug.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "댓글 API", description = "댓글 CRUD 관련 API")
public interface CommentApi {

    @Operation(summary = "댓글 목록 조회", description = "게시글 ID로 댓글 목록을 조회합니다.")
    CommonResponse<CommentResponseDTO.CommentAllResponse> getComments(@PathVariable("boardId") Long boardId, @RequestParam(required = false) Long lastId,
                                                                      @RequestParam(defaultValue = "10") int limit,
                                                                      @RequestParam(defaultValue = "DESC") String order,
                                                                      @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "댓글 생성", description = "게시글에 새로운 댓글을 생성합니다.")
    CommonResponse<CommentResponseDTO.CommentResponse> createComment(
            @PathVariable("boardId") Long boardId,
            @RequestBody CommentRequestDTO.CommentCreateRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "댓글 수정", description = "댓글 ID로 특정 댓글을 수정합니다.")
    CommonResponse<CommentResponseDTO.CommentResponse> updateComment(
            @PathVariable("boardId") Long boardId,
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentRequestDTO.CommentUpdateRequest request);

    @Operation(summary = "댓글 삭제", description = "댓글 ID로 특정 댓글을 삭제합니다.")
    CommonResponse<Boolean> deleteComment(
            @PathVariable("boardId") Long boardId,
            @PathVariable("commentId") Long commentId);
}