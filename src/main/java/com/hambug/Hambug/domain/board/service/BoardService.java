package com.hambug.Hambug.domain.board.service;


import com.hambug.Hambug.domain.board.dto.BoardRequestDTO;
import com.hambug.Hambug.domain.board.dto.BoardResponseDTO;
import com.hambug.Hambug.domain.board.entity.Board;
import com.hambug.Hambug.domain.board.repository.BoardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public List<BoardResponseDTO.BoardResponse> findAllBoards() {
        return boardRepository.findAll().stream()
                .map(BoardResponseDTO.BoardResponse::new)
                .collect(Collectors.toList());
    }

    public BoardResponseDTO.BoardResponse findBoardById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        return new BoardResponseDTO.BoardResponse(board);
    }

    @Transactional // 쓰기 작업에 대한 트랜잭션
    public BoardResponseDTO.BoardResponse createBoard(BoardRequestDTO.BoardCreateRequest request) {
        Board board = new Board(request.title(), request.content());
        boardRepository.save(board);
        return new BoardResponseDTO.BoardResponse(board);
    }

    @Transactional
    public BoardResponseDTO.BoardResponse updateBoard(Long id, BoardRequestDTO.BoardUpdateRequest request) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        board.update(request.title(),  request.content());
        return new BoardResponseDTO.BoardResponse(board);
    }

    @Transactional
    public void deleteBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        boardRepository.delete(board);
    }
}