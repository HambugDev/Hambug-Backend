package com.hambug.Hambug.domain.board.repository;

import com.hambug.Hambug.domain.board.entity.Category;
import com.hambug.Hambug.domain.mypage.dto.MyPageResponseDto;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface CustomBoardRepository {

    Slice<MyPageResponseDto.MyBoardResponse> findByUserIdSlice(Long userId, Long lastId, int limit, String order);

    Slice<Tuple> findAllSlice(Long lastId, int limit, String order, Category category);

    List<Tuple> findAllByIds(List<Long> ids);
}
