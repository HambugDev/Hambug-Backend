package com.hambug.Hambug.domain.oauth.service.impl;

import com.hambug.Hambug.domain.oauth.service.Oauth2UserInfo;
import com.hambug.Hambug.domain.user.entity.LoginType;

import java.util.Map;

public class AppleUserInfo implements Oauth2UserInfo {

    private final Map<String, Object> attributes;

    public AppleUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getId() {
        Object sub = attributes.get("sub");
        return sub == null ? null : String.valueOf(sub);
    }

    @Override
    public String getEmail() {
        Object email = attributes.get("email");
        return email == null ? null : String.valueOf(email);
    }

    @Override
    public String getName() {
        Object name = attributes.get("name");
        if (name != null) return String.valueOf(name);
        Object givenName = attributes.get("given_name");
        Object familyName = attributes.get("family_name");
        if (givenName != null || familyName != null) {
            String g = givenName == null ? "" : String.valueOf(givenName);
            String f = familyName == null ? "" : String.valueOf(familyName);
            String full = (g + " " + f).trim();
            if (!full.isEmpty()) return full;
        }
        String email = getEmail();
        if (email != null) {
            int at = email.indexOf('@');
            return at > 0 ? email.substring(0, at) : email;
        }
        return "AppleUser";
    }

    @Override
    public LoginType getLoginType() {
        return LoginType.APPLE;
    }
}
