package com.hambug.Hambug.global.notification.entity;

import com.hambug.Hambug.domain.user.entity.User;
import com.hambug.Hambug.global.notification.dto.FcmDataType;
import com.hambug.Hambug.global.timeStamped.Timestamped;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User receiver; // 알림을 받는 사람

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FcmDataType type; // 알림 종류 (COMMENT, LIKE 등)

    @Column(nullable = false)
    private Long targetId; // 클릭 시 이동할 게시글 ID 등

    @Column
    private String thumbnailUrl; // 게시글 이미지 URL (있을 경우)

    @Column(nullable = false)
    private boolean isRead = false; // 읽음 여부

    @Builder
    public Notification(User receiver, String title, String content, FcmDataType type, Long targetId, String thumbnailUrl) {
        this.receiver = receiver;
        this.title = title;
        this.content = content;
        this.type = type;
        this.targetId = targetId;
        this.thumbnailUrl = thumbnailUrl;
    }

    public void updateContent(String title, String content) {
        this.title = title;
        this.content = content;
        this.isRead = false; // 새로운 정보가 업데이트되었으므로 읽지 않음 처리
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
