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

    // OIDC용 필드(애플 경로에서 채움)
    private final OidcIdToken idToken;           // null 허용
    private final OidcUserInfo userInfo;         // null 허용
    private final Map<String, Object> attributes; // claims/attributes(불변)

    // 카카오 등 OAuth2 경로용(기존과 동일)
    public PrincipalDetails(UserDto user) {
        this(user, null, null, Collections.emptyMap());
    }

    // 애플 등 OIDC 경로용
    public PrincipalDetails(UserDto user, OidcIdToken idToken, OidcUserInfo userInfo, Map<String, Object> attributes) {
        this.user = user;
        this.idToken = idToken;
        this.userInfo = userInfo;
        this.attributes = attributes == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new HashMap<>(attributes));
    }

    // OAuth2User
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
        // 필요에 맞게 결정: 여기서는 userId가 있으면 그걸 사용
        return user.getUserId() == null ? user.getName() : String.valueOf(user.getUserId());
    }

    // OidcUser
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

    // 편의 메서드(컨트롤러에서 사용 중)
    public UserDto getUserDto() {
        return this.user;
    }
}
