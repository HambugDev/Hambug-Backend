package com.hambug.Hambug.domain.oauth.entity;

import com.hambug.Hambug.domain.admin.dto.AdminUserDto;
import com.hambug.Hambug.domain.user.dto.UserDto;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

@Getter
public class PrincipalDetails implements OAuth2User, OidcUser {

    private final UserDto user;
    private final AdminUserDto admin;

    private final OidcIdToken idToken;
    private final OidcUserInfo userInfo;
    private final Map<String, Object> attributes;

    public PrincipalDetails(UserDto user) {
        this(user, null, null, Collections.emptyMap());
    }

    public PrincipalDetails(AdminUserDto admin) {
        this.user = null;
        this.admin = admin;
        this.idToken = null;
        this.userInfo = null;
        this.attributes = Collections.emptyMap();
    }

    public PrincipalDetails(UserDto user, OidcIdToken idToken, OidcUserInfo userInfo, Map<String, Object> attributes) {
        this.user = user;
        this.admin = null;
        this.idToken = idToken;
        this.userInfo = userInfo;
        this.attributes = attributes == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new HashMap<>(attributes));
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = null;
        if (user != null && user.getRole() != null) {
            role = user.getRole().name();
        } else if (admin != null && admin.getRole() != null) {
            role = admin.getRole().name();
        } else {
            role = (user != null) ? "ROLE_USER" : "ROLE_ADMIN";
        }
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getName() {
        if (user != null) {
            return user.getUserId() == null ? user.getNickname() : String.valueOf(user.getUserId());
        }
        if (admin != null) {
            return admin.getEmail() != null ? admin.getEmail() : admin.getName();
        }
        return "anonymous";
    }

    @Override
    public Map<String, Object> getClaims() {
        return attributes;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public OidcIdToken getIdToken() {
        return idToken;
    }

    public UserDto getUserDto() {
        return this.user;
    }

    public AdminUserDto getAdminDto() {
        return this.admin;
    }
}
