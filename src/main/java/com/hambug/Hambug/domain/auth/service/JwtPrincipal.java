package com.hambug.Hambug.domain.auth.service;

/**
 * A minimal principal abstraction to generate JWTs for different actor types (User, Admin).
 * Optional claims (nickname, platform) can be omitted by returning null.
 */
public interface JwtPrincipal {
    Long getId();
    String getRoleAsString();
    default String getNickname() { return null; }
    default String getPlatform() { return null; }
}
