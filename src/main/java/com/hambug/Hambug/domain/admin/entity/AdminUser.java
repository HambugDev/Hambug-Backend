package com.hambug.Hambug.domain.admin.entity;

import com.hambug.Hambug.domain.user.entity.Role;
import com.hambug.Hambug.global.timeStamped.Timestamped;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminUser extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String salt;

    @Column()
    private Boolean isTempPassword;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "adminUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private EmailVerification emailVerification;

    public static AdminUser admin_of(String name, String email, String hashed, String salt) {
        return AdminUser.builder()
                .name(name)
                .email(email)
                .password(hashed)
                .salt(salt)
                .isTempPassword(false)
                .role(Role.ROLE_SUPER_ADMIN)
                .build();
    }

    public static AdminUser manager_of(String name, String email, String hashed, String salt) {
        return AdminUser.builder()
                .name(name)
                .email(email)
                .password(hashed)
                .salt(salt)
                .isTempPassword(false)
                .role(Role.ROLE_ADMIN)
                .build();
    }

    public boolean isEmailVerification() {
        return this.emailVerification != null && this.emailVerification.isVerified();
    }
}
