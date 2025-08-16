package com.hambug.Hambug.domain.user.entity;

import com.hambug.Hambug.domain.oauth.service.Oauth2UserInfo;
import com.hambug.Hambug.domain.user.dto.UserDto;
import com.hambug.Hambug.global.timeStamped.Timestamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends Timestamped {

    @Column(name = "is_active")
    boolean isActive = false;
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String socialId;
    private String name;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String nickname;
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "ham_rank")
    private Rank rank;

    public static User of(Oauth2UserInfo userInfo, String nickname) {
        return User.builder()
                .email(userInfo.getEmail())
                .socialId(userInfo.getId())
                .name(userInfo.getName())
                .nickname(nickname)
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
                .role(userDto.getRole())
                .isActive(userDto.isActive()).build();
    }
}
