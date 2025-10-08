package com.hambug.Hambug.domain.admin.dto;

import com.hambug.Hambug.domain.admin.entity.AdminUser;
import com.hambug.Hambug.domain.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminUserDto {

    private Long id;
    private String email;
    private String name;
    private Role role;

    public static AdminUserDto of(AdminUser adminUser) {
        return AdminUserDto.builder()
                .id(adminUser.getId())
                .email(adminUser.getEmail())
                .name(adminUser.getName())
                .role(adminUser.getRole())
                .build();
    }
}
