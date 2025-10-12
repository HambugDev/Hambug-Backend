package com.hambug.Hambug.domain.report.service;

import com.hambug.Hambug.domain.admin.dto.AdminReportResponseDto;
import com.hambug.Hambug.domain.board.repository.BoardRepository;
import com.hambug.Hambug.domain.comment.repository.CommentRepository;
import com.hambug.Hambug.domain.report.dto.ReportRequestDTO;
import com.hambug.Hambug.domain.report.entity.Report;
import com.hambug.Hambug.domain.report.entity.TargetType;
import com.hambug.Hambug.domain.report.repository.ReportRepository;
import com.hambug.Hambug.global.exception.ErrorCode;
import com.hambug.Hambug.global.exception.custom.NotFoundException;
import com.hambug.Hambug.global.util.PageParams;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void createReport(Long userId, ReportRequestDTO.CreateReport request) {
        validateTarget(request.targetId(), request.targetType());

        Report report = new Report(
                userId,
                request.targetId(),
                request.targetType(),
                request.reason()
        );

        reportRepository.save(report);
    }

    public Slice<AdminReportResponseDto.ReportGroupSummary> groupedReportPage(TargetType targetType, PageParams params) {
        return reportRepository.fetchGroupedSliceByTargetType(targetType, params);
    }

    public Slice<AdminReportResponseDto.ReportDetail> detailReport(Long id, PageParams pageParams) {
        return reportRepository.getReportSlice(id, pageParams);
    }

    private void validateTarget(Long targetId, TargetType targetType) {
        switch (targetType) {
            case BOARD -> validateBoard(targetId);
            case COMMENT -> validateComment(targetId);
            default -> throw new IllegalArgumentException("Unsupported report target type: " + targetType);
        }
    }

    private void validateBoard(Long targetId) {
        if (!boardRepository.existsById(targetId)) {
            throw new NotFoundException(ErrorCode.BOARD_NOT_FOUND);
        }
    }

    private void validateComment(Long targetId) {
        if (!commentRepository.existsById(targetId)) {
            throw new NotFoundException(ErrorCode.COMMENT_NOT_FOUND);
        }
    }


}