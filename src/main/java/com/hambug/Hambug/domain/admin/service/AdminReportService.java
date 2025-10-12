package com.hambug.Hambug.domain.admin.service;

import com.hambug.Hambug.domain.admin.dto.AdminReportReqDto;
import com.hambug.Hambug.domain.admin.dto.AdminReportResponseDto;
import com.hambug.Hambug.domain.board.service.BoardService;
import com.hambug.Hambug.domain.comment.service.CommentService;
import com.hambug.Hambug.domain.report.entity.TargetType;
import com.hambug.Hambug.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminReportService {

    private final ReportService reportService;
    private final BoardService boardService;
    private final CommentService commentService;

    public AdminReportResponseDto.Page adminReportPage(AdminReportReqDto.AdminReportPageRequest request) {
        Slice<AdminReportResponseDto.ReportGroupSummary> slice = reportService.groupedReportPage(toTargetType(request.type()), request.effectiveParams());
        List<AdminReportResponseDto.GroupedReport> data = slice.getContent().stream()
                .map(s -> new AdminReportResponseDto.GroupedReport(s.targetId(), s.targetType(), s.latestReportId(), s.reportCount()))
                .toList();
        return new AdminReportResponseDto.Page(slice.hasNext(), calculateNextId(slice, data), data);
    }

    public AdminReportResponseDto.DetailPage adminReport(Long id, AdminReportReqDto.AdminReportPageRequest request) {
        Slice<AdminReportResponseDto.ReportDetail> slice = reportService.detailReport(id, request.effectiveParams());
        List<AdminReportResponseDto.ReportDetail> content = slice.getContent();
        return new AdminReportResponseDto.DetailPage(slice.hasNext(), calculateNextId2(slice, content), content);
    }


    private Long calculateNextId(Slice<AdminReportResponseDto.ReportGroupSummary> slice, List<AdminReportResponseDto.GroupedReport> data) {
        Long nextCursorId = null;
        if (slice.hasNext() && !data.isEmpty()) {
            nextCursorId = data.get(data.size() - 1).latestReportId(); // or targetId(), depending on your paging key
        }
        return nextCursorId;
    }

    private Long calculateNextId2(Slice<AdminReportResponseDto.ReportDetail> slice, List<AdminReportResponseDto.ReportDetail> data) {
        Long nextCursorId = null;
        if (slice.hasNext() && !data.isEmpty()) {
            nextCursorId = data.get(data.size() - 1).id(); // or targetId(), depending on your paging key
        }
        return nextCursorId;
    }

    public Boolean deleteReport(Long id, AdminReportReqDto.AdminReportDelete body) {
        TargetType targetType = toTargetType(body.type());
        if (targetType.equals(TargetType.BOARD)) {
            return boardService.deleteBoardForAdmin(id);
        }
        return commentService.deleteCommentForAdmin(id);
    }

    private TargetType toTargetType(Integer type) {
        if (type == null) return null; // null means fetch all types
        return switch (type) {
            case 1 -> TargetType.BOARD;
            default -> TargetType.COMMENT;
        };
    }
}
