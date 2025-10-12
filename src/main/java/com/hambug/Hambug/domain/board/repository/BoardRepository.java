package com.hambug.Hambug.domain.board.repository;

import com.hambug.Hambug.domain.board.entity.Board;
import com.hambug.Hambug.domain.board.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>, CustomBoardRepository {

    List<Board> findByCategory(Category category);

    List<Board> findByOrderByCreatedAtDesc();

    List<Board> findByCategoryOrderByCreatedAtDesc(Category category);
}
