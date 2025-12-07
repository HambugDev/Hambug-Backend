package com.hambug.Hambug.domain.board.api;

import com.hambug.Hambug.domain.board.dto.BoardRequestDTO;
import com.hambug.Hambug.domain.board.dto.BoardResponseDTO;
import com.hambug.Hambug.domain.board.entity.Category;
import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import com.hambug.Hambug.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "게시판 API", description = "커뮤니티 게시판 관리 API")
public interface BoardApi {

    @Operation(summary = "게시글 전체 조회", description = "전체 게시글을 조회합니다.")
    CommonResponse<BoardResponseDTO.BoardAllResponse> getBoards(@RequestParam(required = false) Long lastId,
                                                                @RequestParam(defaultValue = "10") int limit,
                                                                @RequestParam(defaultValue = "DESC") String order);

    @Operation(summary = "카테고리별 게시글 조회", description = "카테고리별로 게시글을 조회합니다.")
    CommonResponse<List<BoardResponseDTO.BoardResponse>> getBoardsByCategory(@RequestParam Category category);

    @Operation(summary = "게시글 상세 조회", description = "게시글 ID로 특정 게시글을 조회합니다. 로그인한 경우 좋아요 여부가 포함됩니다.")
    CommonResponse<BoardResponseDTO.BoardDetailResponse> getBoard(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal(errorOnInvalidType = false) PrincipalDetails principalDetails);

    @Operation(summary = "게시글 생성", description = "새로운 게시글을 생성합니다.")
    CommonResponse<BoardResponseDTO.BoardResponse> createBoard(
            @RequestBody(description = "게시글 생성 요청", required = true,
                    content = @Content(schema = @Schema(implementation = BoardRequestDTO.BoardCreateRequest.class)))
            @org.springframework.web.bind.annotation.RequestBody BoardRequestDTO.BoardCreateRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "이미지와 함께 게시글 생성", description = "이미지 파일과 함께 새로운 게시글을 생성합니다.")
    CommonResponse<BoardResponseDTO.BoardResponse> createBoardWithImages(
            @RequestPart("request") BoardRequestDTO.BoardCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "게시글 수정", description = "기존 게시글을 수정합니다.")
    CommonResponse<BoardResponseDTO.BoardResponse> updateBoard(
            @PathVariable("id") Long id,
            @RequestBody(description = "게시글 수정 요청", required = true,
                    content = @Content(schema = @Schema(implementation = BoardRequestDTO.BoardUpdateRequest.class)))
            @org.springframework.web.bind.annotation.RequestBody BoardRequestDTO.BoardUpdateRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "이미지와 함께 게시글 수정", description = "이미지 파일과 함께 기존 게시글을 수정합니다.")
    CommonResponse<BoardResponseDTO.BoardResponse> updateBoardWithImages(
            @PathVariable("id") Long id,
            @RequestPart("request") BoardRequestDTO.BoardUpdateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    CommonResponse<Boolean> deleteBoard(@PathVariable("id") Long id,
                                        @AuthenticationPrincipal PrincipalDetails principalDetails);
}