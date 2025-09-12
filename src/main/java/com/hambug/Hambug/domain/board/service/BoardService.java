package com.hambug.Hambug.domain.board.service;


import com.hambug.Hambug.domain.board.dto.BoardRequestDTO;
import com.hambug.Hambug.domain.board.dto.BoardResponseDTO;
import com.hambug.Hambug.domain.board.entity.Board;
import com.hambug.Hambug.domain.board.entity.Category;
import com.hambug.Hambug.domain.board.exception.BoardNotFoundException;
import com.hambug.Hambug.domain.board.exception.UnauthorizedBoardAccessException;
import com.hambug.Hambug.domain.board.repository.BoardRepository;
import com.hambug.Hambug.domain.user.entity.User;
import com.hambug.Hambug.domain.user.repository.UserRepository;
import com.hambug.Hambug.global.response.ErrorCode;
import com.hambug.Hambug.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

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
}