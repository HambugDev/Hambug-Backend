package com.hambug.Hambug.domain.user.entity;

import com.hambug.Hambug.domain.oauth.service.Oauth2UserInfo;
import com.hambug.Hambug.domain.user.dto.UserDto;
import com.hambug.Hambug.global.timeStamped.Timestamped;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@EqualsAndHashCode(callSuper = true)
@Entity
@SQLRestriction("deleted_at is null")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends Timestamped {

    @Column(name = "is_active")
    @Builder.Default
    boolean isActive = false;
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String socialId;
    private String name;
    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    private String nickname;
    private String email;
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "ham_rank")
    private Rank rank;

    public static User of(Oauth2UserInfo userInfo, String nickname) {
        return User.builder()
                .email(userInfo.getEmail())
                .socialId(userInfo.getId())
                .name(userInfo.getName())
                .nickname(nickname)
                .loginType(userInfo.getLoginType())
                .profileImageUrl("not yeet")
                .rank(Rank.HAM_BEGINNER)
                .role(Role.ROLE_USER)
                .isActive(true)
                .build();
    }

    public static User toEntity(UserDto userDto) {
        return User.builder()
                .id(userDto.getUserId())
                .email(userDto.getEmail())
                .name(userDto.getName())
                .loginType(userDto.getLoginType())
                .role(userDto.getRole())
                .isActive(userDto.isActive()).build();
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
