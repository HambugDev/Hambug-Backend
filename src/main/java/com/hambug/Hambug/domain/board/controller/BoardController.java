package com.hambug.Hambug.domain.board.controller;


import com.hambug.Hambug.domain.board.api.BoardApi;
import com.hambug.Hambug.domain.board.dto.BoardRequestDTO;
import com.hambug.Hambug.domain.board.dto.BoardResponseDTO;
import com.hambug.Hambug.domain.board.entity.Category;
import com.hambug.Hambug.domain.board.service.BoardService;
import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import com.hambug.Hambug.global.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public CommonResponse<List<BoardResponseDTO.BoardResponse>> getBoards() {
        List<BoardResponseDTO.BoardResponse> boards = boardService.findAllBoards();
        return CommonResponse.ok(boards);
    }

    @Override
    @GetMapping("/category")
    public CommonResponse<List<BoardResponseDTO.BoardResponse>> getBoardsByCategory(@RequestParam Category category) {
        List<BoardResponseDTO.BoardResponse> boards = boardService.findBoardsByCategory(category);
        return CommonResponse.ok(boards);
    }

    @Override
    @GetMapping("/{id}")
    public CommonResponse<BoardResponseDTO.BoardResponse> getBoard(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal(errorOnInvalidType = false) PrincipalDetails principalDetails) {

        Long userId = (principalDetails != null && principalDetails.getUser() != null)
                ? principalDetails.getUser().getUserId()
                : null;

        BoardResponseDTO.BoardResponse board = boardService.findBoardById(id, userId);
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
    @PostMapping("/with-images")
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
}
