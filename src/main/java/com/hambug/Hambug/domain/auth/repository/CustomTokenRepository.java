package com.hambug.Hambug.domain.auth.repository;

import com.hambug.Hambug.domain.auth.entity.Token;

import java.util.List;

public interface CustomTokenRepository {

    List<Token> findAllByUserId(Long userId);
}
