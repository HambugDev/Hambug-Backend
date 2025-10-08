package com.hambug.Hambug.domain.report.controller;

import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import com.hambug.Hambug.domain.report.api.ReportApi;
import com.hambug.Hambug.domain.report.dto.ReportRequestDTO;
import com.hambug.Hambug.domain.report.service.ReportService;
import com.hambug.Hambug.global.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportController implements ReportApi {

    private final ReportService reportService;

    @Override
    public CommonResponse<Void> createReport(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                             @Valid @RequestBody ReportRequestDTO.CreateReport request) {
        Long userId = getUserId(principalDetails);
        reportService.createReport(userId, request);
        return CommonResponse.ok(null);
    }

    private Long getUserId(PrincipalDetails principalDetails) {
        if (principalDetails == null || principalDetails.getUserDto() == null) {
            throw new IllegalStateException("인증 정보가 존재하지 않습니다.");
        }
        return principalDetails.getUserDto().getUserId();
    }
}