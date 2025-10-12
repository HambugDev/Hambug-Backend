package com.hambug.Hambug.domain.board.repository;

import com.hambug.Hambug.domain.mypage.dto.MyPageResponseDto;
import org.springframework.data.domain.Slice;

public interface CustomBoardRepository {

    Slice<MyPageResponseDto.MyBoardResponse> findByUserIdSlice(Long userId, Long lastId, int limit, String order);
}
