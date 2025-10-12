package com.hambug.Hambug.domain.admin.api;

import com.hambug.Hambug.domain.admin.dto.AdminReportReqDto;
import com.hambug.Hambug.domain.admin.dto.AdminReportResponseDto;
import com.hambug.Hambug.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.ModelAttribute;

@Tag(name = "관리자 신고 API", description = "신고된 게시글및 댓글의 대한 검토의 대한 API")
public interface AdminReportApi {

    @Operation(summary = "신고된 내역 목록 조회", description = "무한 페이지네이션 방식으로 신고 내역을 조회합니다. , type = 1(Board) ,type = 2(Comment)")
    CommonResponse<AdminReportResponseDto.Page> getReports(@ModelAttribute AdminReportReqDto.AdminReportPageRequest request);

    @Operation(summary = "신고된 타켓 데이터 상세 조회", description = "신고된 타겟 데이터의 대한 상세 조회를 통해 신고 이유를 조회가능합니다.")
    CommonResponse<AdminReportResponseDto.DetailPage> getReport(Long id, @ModelAttribute AdminReportReqDto.AdminReportPageRequest request);

    @Operation(summary = "신고된 타켓 데이터 삭제", description = "신고된 타겟 데이터의 대해서 삭제 API")
    CommonResponse<Boolean> deleteReport(Long id, AdminReportReqDto.AdminReportDelete body);


}
