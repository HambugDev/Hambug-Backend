package com.hambug.Hambug.domain.like.repository;

import com.hambug.Hambug.domain.board.entity.Board;
import com.hambug.Hambug.domain.like.entity.BoardLike;
import com.hambug.Hambug.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

    /**
     * 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
     */
    boolean existsByUserAndBoard(User user, Board board);

    /**
     * 특정 사용자의 특정 게시글 좋아요 찾기
     */
    Optional<BoardLike> findByUserAndBoard(User user, Board board);

    /**
     * 특정 게시글의 좋아요 개수 조회
     */
    long countByBoard(Board board);

    /**
     * 특정 게시글의 좋아요 개수 조회 (ID로)
     */
    @Query("SELECT COUNT(bl) FROM BoardLike bl WHERE bl.board.id = :boardId")
    long countByBoardId(@Param("boardId") Long boardId);

    /**
     * 특정 게시글에서 가장 최근에 좋아요를 누른 데이터 가져오기
     */
    Optional<BoardLike> findFirstByBoardIdOrderByCreatedAtDesc(Long boardId);

    
    /**
     * 특정 게시글에 대한 모든 좋아요 삭제 (게시글 삭제 시 사용)
     */
    void deleteAllByBoard(Board board);


}
