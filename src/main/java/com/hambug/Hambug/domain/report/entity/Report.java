package com.hambug.Hambug.domain.report.entity;

import com.hambug.Hambug.global.timeStamped.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Report extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId; // 신고한 사용자 ID

    @Column(nullable = false)
    private Long targetId; // 신고된 게시글 또는 댓글 ID

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TargetType targetType; // 신고된 대상의 타입

    @Column(nullable = false)
    private String reason; // 신고 사유

    public Report(Long userId, Long targetId, TargetType targetType, String reason) {
        this.userId = userId;
        this.targetId = targetId;
        this.targetType = targetType;
        this.reason = reason;
    }
}
