package com.hambug.Hambug.domain.auth.repository;

import com.hambug.Hambug.domain.auth.entity.Token;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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
                ).fetch();
    }
}
