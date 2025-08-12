package com.hambug.Hambug.domain.board.controller;


import com.hambug.Hambug.domain.board.api.BoardApi;
import com.hambug.Hambug.domain.board.dto.BoardRequestDTO;
import com.hambug.Hambug.domain.board.dto.BoardResponseDTO;
import com.hambug.Hambug.domain.board.service.BoardService;
import com.hambug.Hambug.global.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/{id}")
    public CommonResponse<BoardResponseDTO.BoardResponse> getBoard(@PathVariable("id") Long id) {
        BoardResponseDTO.BoardResponse board = boardService.findBoardById(id);
        return CommonResponse.ok(board);
    }

    @Override
    @PostMapping
    public CommonResponse<BoardResponseDTO.BoardResponse> createBoard(@RequestBody BoardRequestDTO.BoardCreateRequest request) {
        BoardResponseDTO.BoardResponse createdBoard = boardService.createBoard(request);
        return CommonResponse.ok(createdBoard);
    }

    @Override
    @PutMapping("/{id}")
    public CommonResponse<BoardResponseDTO.BoardResponse> updateBoard(
            @PathVariable("id") Long id,
            @RequestBody BoardRequestDTO.BoardUpdateRequest request) {
        BoardResponseDTO.BoardResponse updatedBoard = boardService.updateBoard(id, request);
        return CommonResponse.ok(updatedBoard);
    }

    @Override
    @DeleteMapping("/{id}")
    public CommonResponse<Boolean> deleteBoard(@PathVariable("id") Long id) {
        // 게시글 삭제 로직
        boardService.deleteBoard(id);
        return CommonResponse.ok(true);
    }
}
