package com.hambug.Hambug.domain.admin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Column()
    private boolean isVerified;

    @OneToOne()
    @JoinColumn(name = "admin_user_id", referencedColumnName = "id")
    private AdminUser adminUser;

    public static EmailVerification of(AdminUser adminUser) {
        return EmailVerification.builder()
                .adminUser(adminUser)
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .isVerified(false)
                .code(String.format("%06d", (int) (Math.random() * 1000000)))
                .build();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    public String resent() {
        this.expiredAt = LocalDateTime.now().plusMinutes(5);
        this.code = String.format("%06d", (int) (Math.random() * 1000000));
        return this.code;
    }
}
