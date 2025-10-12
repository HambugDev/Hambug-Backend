package com.hambug.Hambug.domain.mypage.api;

import com.hambug.Hambug.domain.mypage.dto.MyPageRequestDto;
import com.hambug.Hambug.domain.mypage.dto.MyPageResponseDto;
import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import com.hambug.Hambug.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;

@Tag(name = "마이페이지 API", description = "사용자의 마이페이지 관련 API (내 게시글, 내 댓글 조회 등)")
public interface MyPageApi {

    @Operation(
            summary = "내 게시글 목록 조회",
            description = """
                    로그인한 사용자가 작성한 게시글 목록을 커서 기반 페이지네이션 방식으로 조회합니다.
                    
                    - `lastId`: 마지막으로 조회한 게시글 ID (커서)
                    - `limit`: 한 번에 가져올 게시글 수
                    - `order`: 정렬 기준 (asc 또는 desc)
                    - 인증된 사용자 본인만 조회 가능
                    """
    )
    CommonResponse<MyPageResponseDto.BoardPage> myBoards(
            @Parameter(description = "게시글 조회 쿼리 파라미터 (커서, 정렬, 제한 등)")
            @ModelAttribute MyPageRequestDto.MyBoardRequest query,

            @Parameter(hidden = true, description = "인증된 사용자 정보 (Spring Security)")
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );


    @Operation(
            summary = "내 댓글 목록 조회",
            description = """
                    로그인한 사용자가 작성한 댓글 목록을 커서 기반 페이지네이션 방식으로 조회합니다.
                    
                    - `lastId`: 마지막으로 조회한 댓글 ID (커서)
                    - `limit`: 한 번에 가져올 댓글 수
                    - `order`: 정렬 기준 (asc 또는 desc)
                    - 인증된 사용자 본인만 조회 가능
                    """
    )
    CommonResponse<MyPageResponseDto.CommentPage> myComments(
            @Parameter(description = "댓글 조회 쿼리 파라미터 (커서, 정렬, 제한 등)")
            @ModelAttribute MyPageRequestDto.MyCommentRequest query,

            @Parameter(hidden = true, description = "인증된 사용자 정보 (Spring Security)")
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );
}
