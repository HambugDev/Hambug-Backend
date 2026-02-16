package com.hambug.Hambug.global.notification.entity;

import com.hambug.Hambug.domain.user.dto.UserDto;
import com.hambug.Hambug.domain.user.entity.User;
import com.hambug.Hambug.global.notification.dto.RegisterFcmTokenRequest;
import com.hambug.Hambug.global.timeStamped.Timestamped;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "fcm_device_token", indexes = {
        @Index(name = "idx_fcm_token_user", columnList = "user_id"),
        @Index(name = "idx_fcm_token_active", columnList = "active")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_fcm_token_token", columnNames = {"token"})
})
public class FcmDeviceToken extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fcm_token_id")
    private Long id;

    @Column(nullable = false, length = 512)
    private String token;

    @Column(length = 32)
    private String platform; // ANDROID / IOS / WEB (optional)

    @Column(nullable = false)
    private boolean active;

    private LocalDateTime lastSeenAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    public static FcmDeviceToken of(UserDto user, RegisterFcmTokenRequest request) {
        return FcmDeviceToken.builder()
                .user(User.toEntity(user))
                .token(request.token())
                .platform(request.platform())
                .active(true)
                .lastSeenAt(LocalDateTime.now())
                .build();
    }

    public boolean isOwnedBy(Long userId) {
        return this.user != null && userId != null && userId.equals(this.user.getId());
    }

    public void applyRegistration(RegisterFcmTokenRequest req) {
        this.active = true;
        this.platform = req.platform();
        this.token = req.token();
        touchSeen();
    }

    public void deactivate() {
        this.active = false;
        touchSeen();
    }

    public void touchSeen() {
        this.lastSeenAt = LocalDateTime.now();
    }

    public boolean isSameAs(String token, String platform) {
        return this.token.equals(token) && this.platform.equals(platform);
    }
}