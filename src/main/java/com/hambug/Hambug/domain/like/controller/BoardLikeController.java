package com.hambug.Hambug.domain.like.controller;

import com.hambug.Hambug.domain.like.dto.LikeResponseDTO;
import com.hambug.Hambug.domain.like.service.BoardLikeService;
import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import com.hambug.Hambug.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards/{boardId}/likes")
@Tag(name = "BoardLike", description = "게시글 좋아요 API")
public class BoardLikeController {

    private final BoardLikeService boardLikeService;

    /**
     * 좋아요 토글 (좋아요 추가/취소)
     * POST /api/v1/boards/{boardId}/likes
     */
    @PostMapping
    @Operation(summary = "좋아요 토글", description = "게시글 좋아요를 추가하거나 취소합니다.")
    public CommonResponse<LikeResponseDTO> toggleLike(
            @PathVariable("boardId") Long boardId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Long userId = principalDetails.getUser().getUserId();
        LikeResponseDTO response = boardLikeService.toggleLike(boardId, userId);

        return CommonResponse.ok(response);
    }

    /**
     * 좋아요 정보 조회 (좋아요 여부 + 개수)
     * GET /api/v1/boards/{boardId}/likes
     */
    @GetMapping
    @Operation(summary = "좋아요 정보 조회", description = "게시글의 좋아요 정보를 조회합니다. (좋아요 여부 + 개수)")
    public CommonResponse<LikeResponseDTO> getLikeInfo(
            @PathVariable("boardId") Long boardId,
            @AuthenticationPrincipal(errorOnInvalidType = false) PrincipalDetails principalDetails) {

        Long userId = (principalDetails != null && principalDetails.getUser() != null)
                ? principalDetails.getUser().getUserId()
                : null;

        LikeResponseDTO response = boardLikeService.getLikeInfo(boardId, userId);

        return CommonResponse.ok(response);
    }
}
