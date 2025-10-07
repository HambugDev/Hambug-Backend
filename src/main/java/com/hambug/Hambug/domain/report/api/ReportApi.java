package com.hambug.Hambug.domain.report.api;

import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import com.hambug.Hambug.domain.report.dto.ReportRequestDTO;
import com.hambug.Hambug.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

@Tag(name = "신고 API", description = "게시글/댓글 신고 관련 API")
public interface ReportApi {

    @Operation(summary = "게시글/댓글 신고", description = "게시글 또는 댓글을 신고합니다.")
    @PostMapping
    CommonResponse<Void> createReport(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                      @Valid @RequestBody ReportRequestDTO.CreateReport request);
}