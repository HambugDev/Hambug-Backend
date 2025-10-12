package com.hambug.Hambug.domain.report.repository;

import com.hambug.Hambug.domain.admin.dto.AdminReportResponseDto;
import com.hambug.Hambug.domain.report.entity.TargetType;
import com.hambug.Hambug.global.util.PageParams;
import org.springframework.data.domain.Slice;

public interface CustomReportRepository {
    Slice<AdminReportResponseDto.ReportGroupSummary> fetchGroupedSliceByTargetType(TargetType targetType, PageParams params);

    Slice<AdminReportResponseDto.ReportDetail> getReportSlice(Long id, PageParams params);
}
