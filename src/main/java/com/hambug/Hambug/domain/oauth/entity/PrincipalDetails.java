package com.hambug.Hambug.domain.oauth.entity;

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
    
    private final OidcIdToken idToken;
    private final OidcUserInfo userInfo;
    private final Map<String, Object> attributes;

    public PrincipalDetails(UserDto user) {
        this(user, null, null, Collections.emptyMap());
    }

    public PrincipalDetails(UserDto user, OidcIdToken idToken, OidcUserInfo userInfo, Map<String, Object> attributes) {
        this.user = user;
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
        String role = user.getRole() != null ? user.getRole().name() : "ROLE_USER";
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getName() {
        return user.getUserId() == null ? user.getName() : String.valueOf(user.getUserId());
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
}
