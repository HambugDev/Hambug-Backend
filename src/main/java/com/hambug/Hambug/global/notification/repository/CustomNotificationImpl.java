package com.hambug.Hambug.global.notification.repository;

import com.hambug.Hambug.global.notification.entity.Notification;
import com.hambug.Hambug.global.notification.entity.QNotification;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomNotificationImpl implements CustomNotification {

    private final JPAQueryFactory factory;

    @Override
    public Slice<Notification> findByUserIdSlice(Long userId, Long lastId, int limit) {
        QNotification notification = QNotification.notification;

        BooleanExpression predicate = notification.receiver.id.eq(userId);
        if (lastId != null) {
            predicate = predicate.and(notification.id.lt(lastId));
        }

        List<Notification> results = factory.selectFrom(notification)
                .where(predicate)
                .orderBy(notification.id.desc())
                .limit(limit + 1)
                .fetch();

        boolean hasNext = false;
        if (results.size() > limit) {
            results.remove(limit);
            hasNext = true;
        }

        return new SliceImpl<>(results, PageRequest.of(0, limit), hasNext);
    }
}
