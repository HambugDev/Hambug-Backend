package com.hambug.Hambug.domain.board.controller;


import com.hambug.Hambug.domain.board.api.BoardApi;
import com.hambug.Hambug.domain.board.dto.BoardRequestDTO;
import com.hambug.Hambug.domain.board.dto.BoardResponseDTO;
import com.hambug.Hambug.domain.board.entity.Category;
import com.hambug.Hambug.domain.board.service.BoardService;
import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import com.hambug.Hambug.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardController implements BoardApi {

    private final BoardService boardService;

    @Override
    @GetMapping
    public CommonResponse<BoardResponseDTO.BoardAllResponse> getBoards(@RequestParam(required = false) Long lastId,
                                                                       @RequestParam(defaultValue = "10") int limit,
                                                                       @RequestParam(defaultValue = "DESC") String order) {
        BoardResponseDTO.BoardAllResponse allBoards = boardService.findAllBoards(lastId, limit, order.toLowerCase(), null);
        return CommonResponse.ok(allBoards);

    }

    @Override
    @GetMapping("/category")
    public CommonResponse<BoardResponseDTO.BoardAllResponse> getBoardsByCategory(@RequestParam(required = false) Long lastId,
                                                                                 @RequestParam(defaultValue = "10") int limit,
                                                                                 @RequestParam(defaultValue = "DESC") String order,
                                                                                 @RequestParam Category category) {
        BoardResponseDTO.BoardAllResponse allBoards = boardService.findAllBoards(lastId, limit, order.toLowerCase(), category);
        return CommonResponse.ok(allBoards);
    }

    @Override
    @GetMapping("/{id}")
    public CommonResponse<BoardResponseDTO.BoardDetailResponse> getBoard(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal(errorOnInvalidType = false) PrincipalDetails principalDetails) {

        Long userId = (principalDetails != null && principalDetails.getUser() != null)
                ? principalDetails.getUser().getUserId()
                : null;

        BoardResponseDTO.BoardDetailResponse board = boardService.findBoardById(id, userId);
        return CommonResponse.ok(board);
    }

    @Override
    @PostMapping
    public CommonResponse<BoardResponseDTO.BoardResponse> createBoard(
            @RequestBody BoardRequestDTO.BoardCreateRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        BoardResponseDTO.BoardResponse createdBoard = boardService.createBoard(request, principalDetails.getUser().getUserId());
        return CommonResponse.ok(createdBoard);
    }

    @Override
    @PostMapping(
            value = "/with-images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public CommonResponse<BoardResponseDTO.BoardResponse> createBoardWithImages(
            @RequestPart("request") BoardRequestDTO.BoardCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        BoardResponseDTO.BoardResponse createdBoard = boardService.createBoardWithImages(request, images, principalDetails.getUser().getUserId());
        return CommonResponse.ok(createdBoard);
    }

    @Override
    @PutMapping("/{id}")
    public CommonResponse<BoardResponseDTO.BoardResponse> updateBoard(
            @PathVariable("id") Long id,
            @RequestBody BoardRequestDTO.BoardUpdateRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        BoardResponseDTO.BoardResponse updatedBoard = boardService.updateBoard(id, request, principalDetails.getUser().getUserId());
        return CommonResponse.ok(updatedBoard);
    }

    @Override
    @PutMapping("/{id}/with-images")
    public CommonResponse<BoardResponseDTO.BoardResponse> updateBoardWithImages(
            @PathVariable("id") Long id,
            @RequestPart("request") BoardRequestDTO.BoardUpdateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        BoardResponseDTO.BoardResponse updatedBoard = boardService.updateBoardWithImages(id, request, images, principalDetails.getUser().getUserId());
        return CommonResponse.ok(updatedBoard);
    }

    @Override
    @DeleteMapping("/{id}")
    public CommonResponse<Boolean> deleteBoard(@PathVariable("id") Long id,
                                               @AuthenticationPrincipal PrincipalDetails principalDetails) {
        boardService.deleteBoard(id, principalDetails.getUser().getUserId());
        return CommonResponse.ok(true);
    }


    @GetMapping("/trending")
    @Operation(summary = "인기 게시글 조회", description = "실시간 인기 게시글을 조회합니다. Redis 기반 점수 시스템으로 최근 활발한 게시글이 상위에 노출됩니다.")
    public CommonResponse<List<BoardResponseDTO.BoardResponse>> getTrendingBoards(
            @RequestParam(defaultValue = "5") int limit) {
        List<BoardResponseDTO.BoardResponse> trendingBoards = boardService.findTrendingBoards(limit);
        return CommonResponse.ok(trendingBoards);
    }
}
