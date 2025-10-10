package com.hambug.Hambug.domain.admin.controller;

import com.hambug.Hambug.domain.admin.dto.AdminReportReqDto;
import com.hambug.Hambug.domain.admin.dto.AdminReportResponseDto;
import com.hambug.Hambug.domain.admin.service.AdminReportService;
import com.hambug.Hambug.global.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api/v1/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final AdminReportService adminReportService;

    @GetMapping()
    public CommonResponse<AdminReportResponseDto.Page> getReports(@ModelAttribute AdminReportReqDto.AdminReportPageRequest request) {
        return CommonResponse.ok(adminReportService.adminReportPage(request));
    }

    @GetMapping("/targets/{id}")
    public CommonResponse<AdminReportResponseDto.DetailPage> getReport(@PathVariable("id") Long id, @ModelAttribute AdminReportReqDto.AdminReportPageRequest request) {
        return CommonResponse.ok(adminReportService.adminReport(id, request));
    }

    @DeleteMapping("/targets/{id}")
    public CommonResponse<Boolean> deleteReport(@PathVariable("id") Long id, @RequestBody @Valid AdminReportReqDto.AdminReportDelete body) {
        return CommonResponse.ok(adminReportService.deleteReport(id, body));
    }

}
