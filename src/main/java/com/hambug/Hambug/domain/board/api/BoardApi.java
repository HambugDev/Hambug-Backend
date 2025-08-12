package com.hambug.Hambug.domain.board.api;

import com.hambug.Hambug.domain.board.dto.BoardRequestDTO;
import com.hambug.Hambug.domain.board.dto.BoardResponseDTO;
import com.hambug.Hambug.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "게시판 API", description = "게시판 CRUD 관련 API")
public interface BoardApi {

    @Operation(summary = "모든 게시글 조회", description = "모든 게시글을 조회합니다.")
    CommonResponse<List<BoardResponseDTO.BoardResponse>> getBoards();

    @Operation(summary = "특정 게시글 조회", description = "ID를 통해 특정 게시글을 조회합니다.")
    CommonResponse<BoardResponseDTO.BoardResponse> getBoard(@PathVariable("id") Long id);

    @Operation(summary = "게시글 생성", description = "새로운 게시글을 생성합니다.")
    CommonResponse<BoardResponseDTO.BoardResponse> createBoard(@RequestBody BoardRequestDTO.BoardCreateRequest request);

    @Operation(summary = "게시글 수정", description = "ID를 통해 특정 게시글을 수정합니다.")
    CommonResponse<BoardResponseDTO.BoardResponse> updateBoard(
            @PathVariable("id") Long id,
            @RequestBody BoardRequestDTO.BoardUpdateRequest request);

    @Operation(summary = "게시글 삭제", description = "ID를 통해 특정 게시글을 삭제합니다.")
    CommonResponse<Boolean> deleteBoard(@PathVariable("id") Long id);
}