package com.hambug.Hambug.domain.comment.repository;

import com.hambug.Hambug.domain.comment.entity.Comment;
import com.hambug.Hambug.domain.comment.entity.QComment;
import com.hambug.Hambug.domain.mypage.dto.MyPageResponseDto;
import com.hambug.Hambug.domain.user.entity.QUser;
import com.querydsl.core.Tuple;
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
public class CustomCommentRepositoryImpl implements CustomCommentRepository {

    private final JPAQueryFactory factory;

    @Override
    public Slice<MyPageResponseDto.MyCommentResponse> findByUserIdSlice(Long userId, Long lastId, int limit, String order) {

        QComment comment = QComment.comment;

        boolean isAsc = "asc".equalsIgnoreCase(order);

        var predicate = comment.user.id.eq(userId);
        if (lastId != null) {
            if (isAsc) {
                predicate = predicate.and(comment.id.gt(lastId));
            } else {
                predicate = predicate.and(comment.id.lt(lastId));
            }
        }

        List<Comment> results = factory.selectFrom(comment)
                .join(QUser.user).on(comment.user.id.eq(QUser.user.id))
                .where(predicate)
                .orderBy(isAsc ? comment.id.asc() : comment.id.desc())
                .limit(limit)
                .fetch();

        boolean hasNext = false;
        if (results.size() > limit) {
            results.remove(limit);
            hasNext = true;
        }

        List<MyPageResponseDto.MyCommentResponse> list = results.stream().map(
                MyPageResponseDto.MyCommentResponse::from
        ).toList();

        return new SliceImpl<>(list, PageRequest.of(0, limit), hasNext);
    }

    @Override
    public Slice<Tuple> findByBoardIdSlice(Long boardId, Long lastId, int limit, String order) {
        QComment comment = QComment.comment;
        QUser user = QUser.user;

        boolean isAsc = "asc".equalsIgnoreCase(order);

        BooleanExpression predicate = null;
        if (lastId != null) {
            predicate = isAsc ? comment.id.gt(lastId) : comment.id.lt(lastId);
        }

        List<Tuple> results = factory.select(
                        comment.id,
                        comment.content,
                        comment.user.id,
                        comment.user.nickname,
                        comment.user.profileImageUrl
                ).from(comment).join(user).on(comment.user.id.eq(user.id))
                .where(comment.board.id.eq(boardId)).where(predicate)
                .orderBy(isAsc ? comment.id.asc() : comment.id.desc())
                .limit(limit + 1)
                .fetch();


        boolean hasNext = false;
        if (results.size() > limit) {
            results = results.subList(0, limit);
            hasNext = true;
        }

        return new SliceImpl<>(results, PageRequest.of(0, limit), hasNext);
    }
}
