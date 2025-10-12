package com.hambug.Hambug.domain.comment.repository;

import com.hambug.Hambug.domain.board.entity.Board;
import com.hambug.Hambug.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByBoardId(Long boardId);


    long countByBoard(Board board);

    long countByBoardId(Long boardId);
}