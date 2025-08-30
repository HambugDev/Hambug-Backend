package com.hambug.Hambug.domain.auth.entity;

import com.hambug.Hambug.domain.user.dto.UserDto;
import com.hambug.Hambug.domain.user.entity.User;
import com.hambug.Hambug.global.timeStamped.Timestamped;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@SQLRestriction("deleted_at is null")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Token extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    public static Token of(String token, LocalDateTime expiredAt, UserDto userDto) {
        return Token.builder()
                .token(token)
                .expiredAt(expiredAt)
                .user(User.toEntity(userDto))
                .build();
    }

    void updateToken(LocalDateTime expiredAt, String token) {
        this.expiredAt = expiredAt;
        this.token = token;
    }
}
