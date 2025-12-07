package com.hambug.Hambug.domain.board.repository;

import com.hambug.Hambug.domain.board.entity.Board;
import com.hambug.Hambug.domain.board.entity.QBoard;
import com.hambug.Hambug.domain.board.entity.QBoardImage;
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

import java.util.ArrayList;
import java.util.List;

import static com.hambug.Hambug.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class CustomBoardRepositoryImpl implements CustomBoardRepository {

    private final JPAQueryFactory factory;

    @Override
    public Slice<MyPageResponseDto.MyBoardResponse> findByUserIdSlice(Long userId, Long lastId, int limit, String order) {
        QBoard board = QBoard.board;

        boolean isAsc = "asc".equalsIgnoreCase(order);

        var predicate = board.user.id.eq(userId);
        if (lastId != null) {
            if (isAsc) {
                predicate = predicate.and(board.id.gt(lastId));
            } else {
                predicate = predicate.and(board.id.lt(lastId));
            }
        }

        List<Board> results = factory.selectFrom(board)
                .join(user).on(board.user.id.eq(user.id))
                .where(predicate)
                .orderBy(isAsc ? board.id.asc() : board.id.desc())
                .limit(limit + 1)
                .fetch();

        boolean hasNext = false;
        if (results.size() > limit) {
            results.remove(limit);
            hasNext = true;
        }
        List<MyPageResponseDto.MyBoardResponse> content =
                results.stream().map(b -> new MyPageResponseDto.MyBoardResponse(
                        b.getId(),
                        b.getTitle(),
                        b.getContent(),
                        b.getCategory(),
                        b.getImageUrls() != null ? new ArrayList<>(b.getImageUrls()) : java.util.List.of(),
                        b.getCreatedAt()
                )).toList();


        return new SliceImpl<>(content, PageRequest.of(0, limit), hasNext);
    }

    @Override
    public Slice<Tuple> findAllSlice(Long lastId, int limit, String order) {
        QBoard board = QBoard.board;
        QUser user = QUser.user;
        QBoardImage boardImage = QBoardImage.boardImage;

        boolean isAsc = "asc".equalsIgnoreCase(order);

        BooleanExpression predicate = null;
        if (lastId != null) {
            predicate = isAsc ? board.id.gt(lastId) : board.id.lt(lastId);
        }

        List<Tuple> results = factory
                .select(
                        board.id,
                        board.title,
                        board.content,
                        board.category,
                        boardImage.id.imageUrl,
                        user.nickname,
                        user.id,
                        board.createdAt,
                        board.modifiedAt,
                        board.viewCount,
                        board.commentCount
                )
                .from(board)
                .leftJoin(board.user, user)
                .leftJoin(board.images, boardImage)         // 핵심
                .where(predicate)
                .orderBy(isAsc ? board.id.asc() : board.id.desc())
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
