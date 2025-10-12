package com.hambug.Hambug.domain.board.service.trending;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardTrendingService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String TRENDING_KEY = "board:trending";
    private static final double MIN_SCORE = 5.0; // 최소 점수 (이하면 랭킹에서 제거)


    public void incrementScore(Long boardId, double points) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        zSetOps.incrementScore(TRENDING_KEY, boardId.toString(), points);
        log.debug("게시글 {} 점수 +{} 추가", boardId, points);
    }


    public void addViewScore(Long boardId) {
        incrementScore(boardId, 1.0);
    }


    public void addLikeScore(Long boardId) {
        incrementScore(boardId, 3.0);
    }


    public void addCommentScore(Long boardId) {
        incrementScore(boardId, 5.0);
    }


    public List<Long> getTopBoardIds(int limit) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();

        Set<Object> topBoards = zSetOps.reverseRange(TRENDING_KEY, 0, limit - 1);

        if (topBoards == null || topBoards.isEmpty()) {
            return List.of();
        }

        return topBoards.stream()
                .map(obj -> Long.parseLong(obj.toString()))
                .collect(Collectors.toList());
    }


    public Double getScore(Long boardId) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        return zSetOps.score(TRENDING_KEY, boardId.toString());
    }


    public void decreaseHourlyScores() {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();

        Set<Object> allBoards = zSetOps.range(TRENDING_KEY, 0, -1);

        if (allBoards == null || allBoards.isEmpty()) {
            log.info("랭킹에 게시글이 없습니다.");
            return;
        }

        int decreasedCount = 0;
        int removedCount = 0;

        for (Object boardIdObj : allBoards) {
            String boardId = boardIdObj.toString();
            Double currentScore = zSetOps.score(TRENDING_KEY, boardId);

            if (currentScore != null) {
                double newScore = currentScore - 5.0;

                if (newScore < MIN_SCORE) {
                    zSetOps.remove(TRENDING_KEY, boardId);
                    removedCount++;
                    log.debug("게시글 {} 점수 {}로 랭킹에서 제거", boardId, currentScore);
                } else {
                    zSetOps.add(TRENDING_KEY, boardId, newScore);
                    decreasedCount++;
                }
            }
        }

        log.info("매시간 점수 감소 완료: {} 개 감소, {} 개 제거", decreasedCount, removedCount);
    }


    public void decreasePercentageScores() {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();

        Set<Object> allBoards = zSetOps.range(TRENDING_KEY, 0, -1);

        if (allBoards == null || allBoards.isEmpty()) {
            log.info("랭킹에 게시글이 없습니다.");
            return;
        }

        int decreasedCount = 0;
        int removedCount = 0;

        for (Object boardIdObj : allBoards) {
            String boardId = boardIdObj.toString();
            Double currentScore = zSetOps.score(TRENDING_KEY, boardId);

            if (currentScore != null) {
                double newScore = currentScore * 0.8; // 20% 차감 = 80%만 남김

                if (newScore < MIN_SCORE) {
                    // 최소 점수 미만이면 랭킹에서 제거
                    zSetOps.remove(TRENDING_KEY, boardId);
                    removedCount++;
                    log.debug("게시글 {} 점수 {}로 랭킹에서 제거", boardId, currentScore);
                } else {
                    zSetOps.add(TRENDING_KEY, boardId, newScore);
                    decreasedCount++;
                }
            }
        }

        log.info("20% 점수 감소 완료: {} 개 감소, {} 개 제거", decreasedCount, removedCount);
    }


    public void clearRanking() {
        redisTemplate.delete(TRENDING_KEY);
        log.info("랭킹 초기화 완료");
    }
}
