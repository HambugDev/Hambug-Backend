package com.hambug.Hambug.domain.board.service;


import com.hambug.Hambug.domain.board.dto.BoardRequestDTO;
import com.hambug.Hambug.domain.board.dto.BoardResponseDTO;
import com.hambug.Hambug.domain.board.entity.Board;
import com.hambug.Hambug.domain.board.entity.Category;
import com.hambug.Hambug.domain.board.exception.BoardNotFoundException;
import com.hambug.Hambug.domain.board.exception.UnauthorizedBoardAccessException;
import com.hambug.Hambug.domain.board.repository.BoardRepository;
import com.hambug.Hambug.domain.board.service.trending.BoardTrendingService;
import com.hambug.Hambug.domain.comment.repository.CommentRepository;
import com.hambug.Hambug.domain.like.repository.BoardLikeRepository;
import com.hambug.Hambug.domain.mypage.dto.MyPageRequestDto;
import com.hambug.Hambug.domain.mypage.dto.MyPageResponseDto;
import com.hambug.Hambug.domain.user.entity.User;
import com.hambug.Hambug.domain.user.repository.UserRepository;
import com.hambug.Hambug.global.exception.ErrorCode;
import com.hambug.Hambug.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final BoardLikeRepository boardLikeRepository;
    private final CommentRepository commentRepository;
    private final BoardTrendingService boardTrendingService;

    public List<BoardResponseDTO.BoardResponse> findAllBoards() {
        return boardRepository.findAll().stream()
                .map(BoardResponseDTO.BoardResponse::new)
                .collect(Collectors.toList());
    }

    public List<BoardResponseDTO.BoardResponse> findBoardsByCategory(Category category) {
        return boardRepository.findByCategory(category).stream()
                .map(BoardResponseDTO.BoardResponse::new)
                .collect(Collectors.toList());
    }

    public BoardResponseDTO.BoardResponse findBoardById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException(ErrorCode.BOARD_NOT_FOUND));
        return new BoardResponseDTO.BoardResponse(board);
    }

    @Transactional
    public BoardResponseDTO.BoardResponse findBoardById(Long id, Long userId) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException(ErrorCode.BOARD_NOT_FOUND));

        board.incrementViewCount();

        boardTrendingService.addViewScore(id);

        long likeCount = boardLikeRepository.countByBoardId(id);
        boolean isLiked = false;

        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                isLiked = boardLikeRepository.existsByUserAndBoard(user, board);
            }
        }

        return new BoardResponseDTO.BoardResponse(board, likeCount, isLiked);
    }

    @Transactional
    public BoardResponseDTO.BoardResponse createBoard(BoardRequestDTO.BoardCreateRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Board board = Board.builder()
                .title(request.title())
                .content(request.content())
                .category(request.category())
                .imageUrls(request.imageUrls())
                .user(user)
                .build();

        boardRepository.save(board);
        return new BoardResponseDTO.BoardResponse(board);
    }

    @Transactional
    public BoardResponseDTO.BoardResponse createBoardWithImages(BoardRequestDTO.BoardCreateRequest request,
                                                                List<MultipartFile> images, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<String> imageUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            if (images.size() > 5) {
                throw new IllegalArgumentException("이미지는 최대 5장까지 업로드 가능합니다.");
            }
            for (MultipartFile image : images) {
                try {
                    String imageUrl = s3Service.uploadImage(image);
                    imageUrls.add(imageUrl);
                } catch (Exception e) {
                    throw new RuntimeException("이미지 업로드에 실패했습니다.", e);
                }
            }
        }

        Board board = Board.builder()
                .title(request.title())
                .content(request.content())
                .category(request.category())
                .imageUrls(imageUrls)
                .user(user)
                .build();

        boardRepository.save(board);
        return new BoardResponseDTO.BoardResponse(board);
    }

    @Transactional
    public BoardResponseDTO.BoardResponse updateBoard(Long id, BoardRequestDTO.BoardUpdateRequest request, Long userId) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getUser().getId().equals(userId)) {
            throw new UnauthorizedBoardAccessException(ErrorCode.UNAUTHORIZED_BOARD_ACCESS);
        }

        board.update(request.title(), request.content(), request.category(), request.imageUrls());
        return new BoardResponseDTO.BoardResponse(board);
    }

    @Transactional
    public BoardResponseDTO.BoardResponse updateBoardWithImages(Long id, BoardRequestDTO.BoardUpdateRequest request,
                                                                List<MultipartFile> images, Long userId) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getUser().getId().equals(userId)) {
            throw new UnauthorizedBoardAccessException(ErrorCode.UNAUTHORIZED_BOARD_ACCESS);
        }

        List<String> imageUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            if (images.size() > 5) {
                throw new IllegalArgumentException("이미지는 최대 5장까지 업로드 가능합니다.");
            }
            for (MultipartFile image : images) {
                try {
                    String imageUrl = s3Service.uploadImage(image);
                    imageUrls.add(imageUrl);
                } catch (Exception e) {
                    throw new RuntimeException("이미지 업로드에 실패했습니다.", e);
                }
            }
        }

        board.update(request.title(), request.content(), request.category(), imageUrls);
        return new BoardResponseDTO.BoardResponse(board);
    }

    @Transactional
    public void deleteBoard(Long id, Long userId) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getUser().getId().equals(userId)) {
            throw new UnauthorizedBoardAccessException(ErrorCode.UNAUTHORIZED_BOARD_ACCESS);
        }

        boardRepository.delete(board);
    }

    @Transactional
    public boolean deleteBoardForAdmin(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException(ErrorCode.BOARD_NOT_FOUND));
        boardRepository.delete(board);
        return true;
    }

    @Transactional
    public Slice<MyPageResponseDto.MyBoardResponse> getMyBoards(Long userId, MyPageRequestDto.MyBoardRequest query) {
        return boardRepository.findByUserIdSlice(userId, query.lastId(), query.limit(), query.order());
    }

    public List<BoardResponseDTO.BoardResponse> findTrendingBoards(int limit) {
        List<Long> topBoardIds = boardTrendingService.getTopBoardIds(limit);

        if (topBoardIds.isEmpty()) {
            return List.of();
        }

        List<Board> boards = boardRepository.findAllById(topBoardIds);

        Map<Long, Board> boardMap = boards.stream()
                .collect(Collectors.toMap(Board::getId, board -> board));

        return topBoardIds.stream()
                .map(boardMap::get)
                .filter(board -> board != null)
                .map(board -> {
                    long likeCount = boardLikeRepository.countByBoardId(board.getId());
                    return new BoardResponseDTO.BoardResponse(board, likeCount, false);
                })
                .collect(Collectors.toList());
    }
}
