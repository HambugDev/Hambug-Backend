package com.hambug.Hambug.domain.like.service;

import com.hambug.Hambug.domain.board.entity.Board;
import com.hambug.Hambug.domain.board.repository.BoardRepository;
import com.hambug.Hambug.domain.board.service.trending.BoardTrendingService;
import com.hambug.Hambug.domain.like.dto.LikeResponseDTO;
import com.hambug.Hambug.domain.like.entity.BoardLike;
import com.hambug.Hambug.domain.like.repository.BoardLikeRepository;
import com.hambug.Hambug.domain.user.entity.User;
import com.hambug.Hambug.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardLikeService {

    private final BoardLikeRepository boardLikeRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardTrendingService boardTrendingService;

    @Transactional
    public LikeResponseDTO toggleLike(Long boardId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        boolean isLiked;

        // 이미 좋아요를 눌렀는지 확인
        if (boardLikeRepository.existsByUserAndBoard(user, board)) {
            // 좋아요 취소
            BoardLike boardLike = boardLikeRepository.findByUserAndBoard(user, board)
                    .orElseThrow(() -> new IllegalStateException("좋아요 정보를 찾을 수 없습니다."));
            boardLikeRepository.delete(boardLike);
            isLiked = false;
        } else {
            // 좋아요 추가
            BoardLike boardLike = BoardLike.builder()
                    .user(user)
                    .board(board)
                    .build();
            boardLikeRepository.save(boardLike);
            isLiked = true;

            boardTrendingService.addLikeScore(boardId);
        }

        long likeCount = boardLikeRepository.countByBoard(board);

        return LikeResponseDTO.of(boardId, isLiked, likeCount);
    }

    /**
     * 특정 게시글의 좋아요 개수 조회
     */
    public long getLikeCount(Long boardId) {
        return boardLikeRepository.countByBoardId(boardId);
    }

    /**
     * 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
     */
    public boolean isLikedByUser(Long boardId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        return boardLikeRepository.existsByUserAndBoard(user, board);
    }

    /**
     * 게시글 좋아요 정보 조회 (좋아요 여부 + 개수)
     */
    public LikeResponseDTO getLikeInfo(Long boardId, Long userId) {
        boolean isLiked = false;

        if (userId != null) {
            isLiked = isLikedByUser(boardId, userId);
        }

        long likeCount = getLikeCount(boardId);

        return LikeResponseDTO.of(boardId, isLiked, likeCount);
    }
}
