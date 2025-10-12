package com.hambug.Hambug.domain.admin.dto;

import com.hambug.Hambug.domain.report.entity.TargetType;

import java.time.LocalDateTime;
import java.util.List;

public class AdminReportResponseDto {

    public record GroupedReport(Long targetId, TargetType targetType, Long latestReportId, Long reportCount) {
    }

    public record Page(Boolean nextPage, Long netCursorId, List<GroupedReport> content) {
    }

    public record DetailPage(Boolean nextPage, Long netCursorId, List<ReportDetail> content) {
    }

    public record DetailReport(Long id, String title, String content, String authorNickname, Long authorId) {
    }

    public record ReportGroupSummary(
            Long targetId,
            TargetType targetType,
            Long latestReportId,
            Long reportCount
    ) {
    }

    public record ReportDetail(
            Long id,
            Long authorId,
            String nickname,
            String reason,
            LocalDateTime createdAt
    ) {
    }

    public record ReportGroupSummaryWithTarget(
            Long targetId,
            TargetType targetType,
            Long latestReportId,
            Long reportCount,
            String boardTitle,   // BOARD일 때
            String commentContent // COMMENT일 때
    ) {
    }
}
