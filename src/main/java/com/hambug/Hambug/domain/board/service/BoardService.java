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
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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

    @Transactional(readOnly = true)
    public BoardResponseDTO.BoardAllResponse findAllBoards(Long lastId, int limit, String order, Category category) {

        var slice = boardRepository.findAllSlice(lastId, limit, order, category);

        return getBoardAllResponse(slice);
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
    public BoardResponseDTO.BoardDetailResponse findBoardById(Long id, Long userId) {
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

        return new BoardResponseDTO.BoardDetailResponse(board, likeCount, isLiked);
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

    @Transactional(readOnly = true)
    public List<BoardResponseDTO.BoardResponse> findTrendingBoards(int limit) {
        List<Long> topBoardIds = boardTrendingService.getTopBoardIds(limit);
        if (topBoardIds.isEmpty()) {
            return List.of();
        }
        List<Tuple> allByIds = boardRepository.findAllByIds(topBoardIds);
        return getBoardResponses(allByIds);
    }

    private BoardResponseDTO.@NonNull BoardAllResponse getBoardAllResponse(Slice<Tuple> slice) {
        var boardResponses = getBoardResponses(slice.getContent());

        Long nextCursorId = boardResponses.isEmpty() ? null : boardResponses.get(boardResponses.size() - 1).id();
        Boolean hasNext = slice.hasNext();

        return new BoardResponseDTO.BoardAllResponse(boardResponses, nextCursorId, hasNext);
    }

    private @NonNull ArrayList<BoardResponseDTO.BoardResponse> getBoardResponses(List<Tuple> datas) {
        var boardMap = new LinkedHashMap<Long, BoardResponseDTO.BoardResponse>();
        var imageCountMap = new LinkedHashMap<Long, Long>();

        datas.forEach(tuple -> {

            var boardId = tuple.get(0, Long.class);
            var title = tuple.get(1, String.class);
            var content = tuple.get(2, String.class);
            var _category = tuple.get(3, Category.class);
            // 이미지 URL은 집계로 대체하여 별도 조회할 예정이므로 여기서는 사용하지 않음
            var nickname = tuple.get(4, String.class);
            var authorId = tuple.get(5, Long.class);
            var createdAt = tuple.get(6, LocalDateTime.class);
            var updatedAt = tuple.get(7, LocalDateTime.class);
            var viewCount = tuple.get(8, Long.class);
            var commentCount = tuple.get(9, Long.class);
            var likeCount = tuple.get(10, Long.class);
            var imageCount = tuple.get(11, Long.class);

            boardMap.computeIfAbsent(boardId, key ->
                    new BoardResponseDTO.BoardResponse(
                            boardId,
                            title,
                            content,
                            _category,
                            new ArrayList<>(),
                            nickname,
                            authorId,
                            createdAt,
                            updatedAt,
                            viewCount,
                            likeCount != null ? likeCount : 0L,
                            commentCount,
                            false
                    ));

            // 이미지 개수 기록 (null 안전 처리)
            imageCountMap.put(boardId, imageCount != null ? imageCount : 0L);
        });

        // 이미지가 있는 게시글만 별도 조회하여 이미지 URL 목록 채우기
        imageCountMap.forEach((bId, cnt) -> {
            if (cnt != null && cnt > 0) {
                // 필요 시 개별 조회 (간단 구현, 추후 배치 조회로 최적화 가능)
                boardRepository.findById(bId).ifPresent(board -> {
                    var urls = board.getImageUrls();
                    if (urls != null && !urls.isEmpty()) {
                        boardMap.get(bId).imageUrls().addAll(urls);
                    }
                });
            }
        });

        return new ArrayList<>(boardMap.values());
    }
}
