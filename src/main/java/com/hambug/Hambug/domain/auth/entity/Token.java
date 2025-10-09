package com.hambug.Hambug.domain.auth.entity;

import com.hambug.Hambug.domain.admin.entity.AdminUser;
import com.hambug.Hambug.domain.oauth.service.Oauth2UserInfo;
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

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = true)
    private TokenType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @OneToOne()
    @JoinColumn(name = "admin_user_id", referencedColumnName = "id")
    private AdminUser adminUser;

    public static Token of(String token, LocalDateTime expiredAt, UserDto userDto) {
        return Token.builder()
                .token(token)
                .expiredAt(expiredAt)
                .user(User.toEntity(userDto))
                .type(TokenType.REFRESH_TOKEN)
                .build();
    }

    public static Token socialOf(Oauth2UserInfo userInfo, UserDto userDto) {
        TokenBuilder builder = Token.builder()
                .token(userInfo.getRefreshToken())
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .user(User.toEntity(userDto));
        if (userInfo.getLoginType() == com.hambug.Hambug.domain.user.entity.LoginType.KAKAO) {
            builder.type(TokenType.KAKAO_REFRESH_TOKEN);
        } else {
            builder.type(TokenType.APPLE_REFRESH_TOKEN);
        }
        return builder.build();
    }

    public static Token of(String token, LocalDateTime expiredAt, AdminUser user) {
        return Token.builder()
                .token(token)
                .expiredAt(expiredAt)
                .adminUser(user)
                .build();
    }


    void updateToken(LocalDateTime expiredAt, String token) {
        this.expiredAt = expiredAt;
        this.token = token;
    }
}
