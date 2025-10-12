package com.hambug.Hambug.domain.mypage.service;

import com.hambug.Hambug.domain.board.service.BoardService;
import com.hambug.Hambug.domain.comment.service.CommentService;
import com.hambug.Hambug.domain.mypage.dto.MyPageRequestDto;
import com.hambug.Hambug.domain.mypage.dto.MyPageResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyPageService {

    private final BoardService boardService;
    private final CommentService commentService;


    public MyPageResponseDto.BoardPage getMyBoards(MyPageRequestDto.MyBoardRequest query, Long userId) {
        Slice<MyPageResponseDto.MyBoardResponse> slice = boardService.getMyBoards(userId, query);
        return MyPageResponseDto.BoardPage.from(slice.getContent(), slice.hasNext());
    }


    public MyPageResponseDto.CommentPage getMyComments(MyPageRequestDto.MyCommentRequest query, Long userId) {
        Slice<MyPageResponseDto.MyCommentResponse> slice = commentService.getMyComments(userId, query);
        return MyPageResponseDto.CommentPage.from(slice.getContent(), slice.hasNext());
    }
}
