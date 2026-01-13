package com.hambug.Hambug.domain.comment.service;

import com.hambug.Hambug.domain.board.entity.Board;
import com.hambug.Hambug.domain.board.repository.BoardRepository;
import com.hambug.Hambug.domain.board.service.trending.BoardTrendingService;
import com.hambug.Hambug.domain.comment.dto.CommentRequestDTO;
import com.hambug.Hambug.domain.comment.dto.CommentResponseDTO;
import com.hambug.Hambug.domain.comment.entity.Comment;
import com.hambug.Hambug.domain.comment.repository.CommentRepository;
import com.hambug.Hambug.domain.mypage.dto.MyPageRequestDto;
import com.hambug.Hambug.domain.mypage.dto.MyPageResponseDto;
import com.hambug.Hambug.domain.user.entity.User;
import com.hambug.Hambug.domain.user.service.UserService;
import com.hambug.Hambug.global.event.CommentCreatedEvent;
import com.hambug.Hambug.global.exception.ErrorCode;
import com.hambug.Hambug.global.exception.custom.NotFoundException;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserService userService;
    private final BoardTrendingService boardTrendingService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public CommentResponseDTO.CommentAllResponse findCommentsByBoard(Long boardId, Long userId, Long lastId, int limit, String order) {

        validateBoard(boardId);

        Slice<Tuple> slice = commentRepository.findByBoardIdSlice(boardId, lastId, limit, order);

        List<CommentResponseDTO.CommentResponse> comments = slice.getContent().stream()
                .map(tuple -> {
                    Long authorId = tuple.get(2, Long.class);
                    return new CommentResponseDTO.CommentResponse(
                            tuple.get(0, Long.class),
                            tuple.get(1, String.class),
                            authorId,
                            tuple.get(3, String.class),
                            tuple.get(4, String.class),
                            authorId != null && authorId.equals(userId),
                            tuple.get(5, LocalDateTime.class),
                            tuple.get(6, LocalDateTime.class)
                    );
                })
                .toList();

        Long nextCursorId = comments.isEmpty() ? null : comments.get(comments.size() - 1).id();
        Boolean hasNext = slice.hasNext();

        return new CommentResponseDTO.CommentAllResponse(comments, nextCursorId, hasNext);
    }


    @Transactional
    public CommentResponseDTO.CommentResponse createComment(Long boardId, Long userId, CommentRequestDTO.CommentCreateRequest request) {
        Board board = findBoard(boardId);
        User user = User.toEntity(userService.getById(userId));
        Comment comment = new Comment(request.content(), board, user);
        commentRepository.save(comment);

        sendCommentNotification(board, user, request.content());
        boardTrendingService.addCommentScore(boardId);
        board.incrementCommentCount();

        return new CommentResponseDTO.CommentResponse(comment, true);
    }

    private void sendCommentNotification(Board board, User commentAuthor, String commentContent) {
        User boardAuthor = board.getUser();
        log.info("사용자 닉네이마, !! : {}", commentAuthor.getNickname());

        if (boardAuthor.getId().equals(commentAuthor.getId())) {
            return;
        }
        eventPublisher.publishEvent(new CommentCreatedEvent(board.getId(), boardAuthor.getId(), commentAuthor.getNickname(), commentContent));
    }

    @Transactional
    public CommentResponseDTO.CommentResponse updateComment(Long boardId, Long commentId, CommentRequestDTO.CommentUpdateRequest request) {
        Comment comment = findComment(commentId);
        validateCommentBoard(comment, boardId);

        comment.update(request.content());
        return new CommentResponseDTO.CommentResponse(comment, true);
    }

    @Transactional
    public void deleteComment(Long boardId, Long commentId) {
        Comment comment = findComment(commentId);
        validateCommentBoard(comment, boardId);
        comment.getBoard().decrementCommentCount();
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

    public boolean deleteCommentForAdmin(Long id) {
        Comment comment = findComment(id);
        commentRepository.delete(comment);
        return true;
    }

    @Transactional
    public Slice<MyPageResponseDto.MyCommentResponse> getMyComments(Long userId, MyPageRequestDto.MyCommentRequest query) {
        return commentRepository.findByUserIdSlice(userId, query.lastId(), query.limit(), query.order());
    }

}