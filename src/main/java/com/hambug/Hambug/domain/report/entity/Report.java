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
    private Long userId;

    @Column(nullable = false)
    private Long targetId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String reason;

    public Report(Long userId, Long targetId, TargetType targetType, String title, String reason) {
        this.userId = userId;
        this.targetId = targetId;
        this.targetType = targetType;
        this.title = title;
        this.reason = reason;
    }
}
