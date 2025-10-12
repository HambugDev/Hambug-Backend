package com.hambug.Hambug.domain.comment.repository;

import com.hambug.Hambug.domain.mypage.dto.MyPageResponseDto;
import org.springframework.data.domain.Slice;

public interface CustomCommentRepository {

    Slice<MyPageResponseDto.MyCommentResponse> findByUserIdSlice(Long userId, Long lastId, int limit, String order);
}
