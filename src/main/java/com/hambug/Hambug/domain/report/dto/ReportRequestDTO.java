package com.hambug.Hambug.domain.report.dto;

import com.hambug.Hambug.domain.report.entity.TargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReportRequestDTO {

    public record CreateReport(
            @NotNull(message = "신고 대상 ID는 필수입니다.")
            Long targetId,

            @NotNull(message = "신고 대상 타입은 필수입니다.")
            TargetType targetType,

            @NotBlank(message = "신고 제목은 필수 입니다.")
            String title,

            @NotBlank(message = "신고 사유는 필수입니다.")
            String reason
    ) {
    }
}
