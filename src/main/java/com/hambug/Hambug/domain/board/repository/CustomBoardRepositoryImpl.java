package com.hambug.Hambug.domain.board.repository;

import com.hambug.Hambug.domain.board.entity.Board;
import com.hambug.Hambug.domain.board.entity.Category;
import com.hambug.Hambug.domain.board.entity.QBoard;
import com.hambug.Hambug.domain.board.entity.QBoardImage;
import com.hambug.Hambug.domain.like.entity.QBoardLike;
import com.hambug.Hambug.domain.mypage.dto.MyPageResponseDto;
import com.hambug.Hambug.domain.user.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class CustomBoardRepositoryImpl implements CustomBoardRepository {

    private final JPAQueryFactory factory;

    @Override
    public Slice<MyPageResponseDto.MyBoardResponse> findByUserIdSlice(Long userId, Long lastId, int limit, String order) {
        QBoard board = QBoard.board;
        QBoardLike boardLike = QBoardLike.boardLike;

        boolean isAsc = "asc".equalsIgnoreCase(order);

        var predicate = board.user.id.eq(userId);
        if (lastId != null) {
            if (isAsc) {
                predicate = predicate.and(board.id.gt(lastId));
            } else {
                predicate = predicate.and(board.id.lt(lastId));
            }
        }

        List<Tuple> results = factory
                .select(
                        board,
                        boardLike.id.countDistinct().as("likeCount")
                )
                .from(board)
                .leftJoin(boardLike).on(boardLike.board.id.eq(board.id))
                .where(predicate)
                .groupBy(board.id)
                .orderBy(isAsc ? board.id.asc() : board.id.desc())
                .limit(limit + 1)
                .fetch();

        boolean hasNext = false;
        if (results.size() > limit) {
            results.remove(limit);
            hasNext = true;
        }

        List<MyPageResponseDto.MyBoardResponse> content =
                results.stream().map(tuple -> {
                    Board b = tuple.get(board);
                    Long likeCount = tuple.get(1, Long.class);
                    return MyPageResponseDto.MyBoardResponse.from(Objects.requireNonNull(b), likeCount);
                }).toList();

        return new SliceImpl<>(content, PageRequest.of(0, limit), hasNext);
    }

    @Override
    public Slice<Tuple> findAllSlice(Long lastId, int limit, String order, Category category) {
        QBoard board = QBoard.board;
        QUser user = QUser.user;
        QBoardImage boardImage = QBoardImage.boardImage;
        QBoardLike boardLike = QBoardLike.boardLike;

        boolean isAsc = "asc".equalsIgnoreCase(order);

        BooleanBuilder builder = new BooleanBuilder();

        // 페이징 조건
        if (lastId != null) {
            builder.and(isAsc ? board.id.gt(lastId) : board.id.lt(lastId));
        }

        // 카테고리 조건
        if (category != null) {
            builder.and(board.category.eq(category));
        }

        List<Tuple> results = factory
                .select(
                        board.id,
                        board.title,
                        board.content,
                        board.category,
                        user.nickname,
                        user.id,
                        board.createdAt,
                        board.modifiedAt,
                        board.viewCount,
                        board.commentCount,
                        boardLike.id.countDistinct().as("likeCount"),
                        boardImage.id.countDistinct().as("imageCount")

                )
                .from(board)
                .leftJoin(board.user, user)
                .leftJoin(board.images, boardImage)
                .leftJoin(boardLike).on(boardLike.board.id.eq(board.id))
                .where(builder)
                .orderBy(isAsc ? board.createdAt.asc() : board.createdAt.desc())
                .groupBy(board.id)
                .limit(limit + 1)
                .fetch();

        boolean hasNext = false;
        if (results.size() > limit) {
            results = results.subList(0, limit);
            hasNext = true;
        }

        return new SliceImpl<>(results, PageRequest.of(0, limit), hasNext);
    }


    public List<Tuple> findAllByIds(List<Long> ids) {
        QBoard board = QBoard.board;
        QUser user = QUser.user;
        QBoardImage boardImage = QBoardImage.boardImage;
        QBoardLike boardLike = QBoardLike.boardLike;

        return factory
                .select(
                        board.id,
                        board.title,
                        board.content,
                        board.category,
                        user.nickname,
                        user.id,
                        board.createdAt,
                        board.modifiedAt,
                        board.viewCount,
                        board.commentCount,
                        boardLike.id.countDistinct().as("likeCount"),
                        boardImage.id.countDistinct().as("imageCount")

                )
                .from(board)
                .leftJoin(board.user, user)
                .leftJoin(board.images, boardImage)
                .leftJoin(boardLike).on(boardLike.board.id.eq(board.id))
                .where(board.id.in(ids))
                .groupBy(board.id)
                .fetch();
    }
}
