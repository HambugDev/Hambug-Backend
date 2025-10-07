package com.hambug.Hambug.domain.comment.service;

import com.hambug.Hambug.domain.board.entity.Board;
import com.hambug.Hambug.domain.board.repository.BoardRepository;
import com.hambug.Hambug.domain.comment.dto.CommentRequestDTO;
import com.hambug.Hambug.domain.comment.dto.CommentResponseDTO;
import com.hambug.Hambug.domain.comment.entity.Comment;
import com.hambug.Hambug.domain.comment.repository.CommentRepository;
import com.hambug.Hambug.global.exception.custom.NotFoundException;
import com.hambug.Hambug.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    @Transactional(readOnly = true)
    public List<CommentResponseDTO.CommentResponse> findCommentsByBoard(Long boardId) {
        validateBoard(boardId);
        return commentRepository.findAllByBoardId(boardId).stream()
                .map(CommentResponseDTO.CommentResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponseDTO.CommentResponse createComment(Long boardId, CommentRequestDTO.CommentCreateRequest request) {
        Board board = findBoard(boardId);
        Comment comment = new Comment(request.content(), board);
        commentRepository.save(comment);
        return new CommentResponseDTO.CommentResponse(comment);
    }

    @Transactional
    public CommentResponseDTO.CommentResponse updateComment(Long boardId, Long commentId, CommentRequestDTO.CommentUpdateRequest request) {
        Comment comment = findComment(commentId);
        validateCommentBoard(comment, boardId);

        comment.update(request.content());
        return new CommentResponseDTO.CommentResponse(comment);
    }

    @Transactional
    public void deleteComment(Long boardId, Long commentId) {
        Comment comment = findComment(commentId);
        validateCommentBoard(comment, boardId);
        commentRepository.delete(comment);
    }

    private void validateBoard(Long boardId) {
        if (!boardRepository.existsById(boardId)) {
            throw new NotFoundException(ErrorCode.BOARD_NOT_FOUND);
        }
    }

    private Board findBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.BOARD_NOT_FOUND));
    }

    private Comment findComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private void validateCommentBoard(Comment comment, Long boardId) {
        if (!comment.getBoard().getId().equals(boardId)) {
            throw new NotFoundException(ErrorCode.COMMENT_BOARD_MISMATCH);
        }
    }
}