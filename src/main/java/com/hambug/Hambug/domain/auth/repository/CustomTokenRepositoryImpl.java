package com.hambug.Hambug.domain.auth.repository;

import com.hambug.Hambug.domain.auth.entity.Token;
import com.hambug.Hambug.domain.auth.entity.TokenType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.hambug.Hambug.domain.auth.entity.QToken.token1;

@Repository
@RequiredArgsConstructor
public class CustomTokenRepositoryImpl implements CustomTokenRepository {

    private final JPAQueryFactory factory;

    @Override
    public List<Token> findAllByUserId(Long userId) {
        return factory.selectFrom(token1)
                .where(
                        token1.user.id.eq(userId)
                                .and(token1.deletedAt.isNull())
                ).fetch();
    }

    @Override
    public Optional<Token> findByUserIdAndType(Long userId, TokenType type) {
        Token token = factory.selectFrom(token1)
                .where(
                        token1.user.id.eq(userId)
                                .and(token1.type.eq(type)
                                        .and(token1.deletedAt.isNull()))
                ).fetchOne();
        return Optional.ofNullable(token);
    }
}
