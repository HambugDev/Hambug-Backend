package com.hambug.Hambug.domain.oauth.entity;

import com.hambug.Hambug.domain.user.dto.UserDto;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
public class PrincipalDetails implements OAuth2User {

    private UserDto user;

    public PrincipalDetails(UserDto user) {
        this.user = user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.user.getRole().toString()));
        return authorities;
    }

    @Override
    public String getName() {
        return user.getName();
    }

    public UserDto getUserDto() {
        return this.user;
    }
}
