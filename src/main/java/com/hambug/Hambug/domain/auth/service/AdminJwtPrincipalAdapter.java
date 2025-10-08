package com.hambug.Hambug.domain.auth.service;

import com.hambug.Hambug.domain.admin.entity.AdminUser;
import lombok.Getter;

@Getter
public class AdminJwtPrincipalAdapter implements JwtPrincipal {

    private final AdminUser adminUser;

    public AdminJwtPrincipalAdapter(AdminUser adminUser) {
        this.adminUser = adminUser;
    }

    @Override
    public Long getId() {
        return adminUser.getId();
    }

    @Override
    public String getRoleAsString() {
        return adminUser.getRole() != null ? adminUser.getRole().name() : null;
    }

    // nickname and platform are not applicable for admin; default nulls from interface are fine.
}
