package com.hambug.Hambug.domain.board.repository;

import com.hambug.Hambug.domain.board.entity.Board;
import com.hambug.Hambug.domain.board.entity.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>, CustomBoardRepository {

    @EntityGraph(attributePaths = {"user", "images"})
    List<Board> findAll();

    @EntityGraph(attributePaths = {"user", "images"})
    List<Board> findByCategory(Category category);

    @EntityGraph(attributePaths = {"user", "images"})
    List<Board> findByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"user", "images"})
    List<Board> findByCategoryOrderByCreatedAtDesc(Category category);

    @EntityGraph(attributePaths = {"user", "images"})
    List<Board> findAllById(Iterable<Long> ids);

    @Query("SELECT b FROM Board b WHERE b.createdAt >= :startDate ORDER BY b.createdAt DESC")
    List<Board> findRecentBoards(@Param("startDate") LocalDateTime startDate, Pageable pageable);
}
