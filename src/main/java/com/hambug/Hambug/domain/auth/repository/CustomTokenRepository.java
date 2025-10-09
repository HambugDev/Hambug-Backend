package com.hambug.Hambug.domain.auth.repository;

import com.hambug.Hambug.domain.auth.entity.Token;
import com.hambug.Hambug.domain.auth.entity.TokenType;

import java.util.List;
import java.util.Optional;

public interface CustomTokenRepository {

    List<Token> findAllByUserId(Long userId);

    Optional<Token> findByUserIdAndType(Long userId, TokenType type);
}
