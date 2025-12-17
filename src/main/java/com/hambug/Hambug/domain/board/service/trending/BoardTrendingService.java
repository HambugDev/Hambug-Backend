package com.hambug.Hambug.domain.board.service.trending;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardTrendingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final com.hambug.Hambug.domain.board.repository.BoardRepository boardRepository;

    // 일자별 버킷 키: board:trending:yyyyMMdd
    private static final String DAILY_KEY_PREFIX = "board:trending:";
    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.BASIC_ISO_DATE; // yyyyMMdd

    // 롤링 윈도우 합산 키(캐시)
    private static final String ROLLING_KEY = "board:trending:rolling:30d";

    private static final int WINDOW_DAYS = 30; // 최근 30일만 인기 집계
    private static final int DAILY_KEY_TTL_DAYS = 40; // 일자 키 TTL(버퍼 포함)
    private static final int ROLLING_TTL_SECONDS = 60; // 롤링 합산 캐시 TTL

    private static final double MIN_SCORE = 5.0; // 최소 점수 (이하면 랭킹에서 제거)

    private String keyFor(LocalDate date) {
        return DAILY_KEY_PREFIX + date.format(DAY_FMT);
    }

    private String todayKey() {
        return keyFor(LocalDate.now());
    }

    /**
     * 점수 적립: 오늘 일자 키에 적립하고 TTL 부여
     */
    public void incrementScore(Long boardId, double points) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        // 게시글 생성일 기준으로 일자 키를 결정
        String key;
        try {
            var boardOpt = boardRepository.findById(boardId);
            if (boardOpt.isEmpty()) {
                log.warn("[Trending] 존재하지 않는 게시글 ID로 점수 적립 시도: {}", boardId);
                return;
            }
            var createdAt = boardOpt.get().getCreatedAt();
            var date = createdAt != null ? createdAt.toLocalDate() : LocalDate.now();
            key = keyFor(date);
        } catch (Exception e) {
            // 예기치 못한 오류 시 안전하게 오늘 키로 폴백
            log.warn("[Trending] 생성일 조회 실패로 오늘 키로 폴백: boardId={}, err={}", boardId, e.toString());
            key = todayKey();
        }
        zSetOps.incrementScore(key, boardId.toString(), points);
        // 존재하지 않거나 TTL 없으면 TTL 설정
        Long ttl = redisTemplate.getExpire(key);
        if (ttl == null || ttl <= 0) {
            redisTemplate.expire(key, Duration.ofDays(DAILY_KEY_TTL_DAYS));
        }
        log.debug("[Trending] {} +{} (key={})", boardId, points, key);
    }

    public void addViewScore(Long boardId) { incrementScore(boardId, 1.0); }
    public void addLikeScore(Long boardId) { incrementScore(boardId, 3.0); }
    public void addCommentScore(Long boardId) { incrementScore(boardId, 5.0); }

    /**
     * 최근 WINDOW_DAYS 일의 키를 ZUNIONSTORE로 합산하여 상위 N 게시글을 반환
     */
    public List<Long> getTopBoardIds(int limit) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();

        LocalDate today = LocalDate.now();
        List<String> keys = new ArrayList<>(WINDOW_DAYS);
        for (int i = 0; i < WINDOW_DAYS; i++) {
            keys.add(keyFor(today.minusDays(i)));
        }

        // 빈 집합 처리를 위해 첫 번째를 base로, 나머지를 others로 합산
        String baseKey = keys.get(0);
        List<String> others = keys.subList(1, keys.size());
        zSetOps.unionAndStore(baseKey, others, ROLLING_KEY);
        redisTemplate.expire(ROLLING_KEY, Duration.ofSeconds(ROLLING_TTL_SECONDS));

        Set<Object> topBoards = zSetOps.reverseRange(ROLLING_KEY, 0, Math.max(0, limit - 1));
        if (topBoards == null || topBoards.isEmpty()) {
            return List.of();
        }
        return topBoards.stream()
                .map(obj -> Long.parseLong(obj.toString()))
                .collect(Collectors.toList());
    }

    /**
     * 특정 게시글의 오늘 기준 점수(오늘 키) 조회(디버그용)
     */
    public Double getScore(Long boardId) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        return zSetOps.score(todayKey(), boardId.toString());
    }

    /**
     * 감쇠 로직(옵션): 오늘 키에 대해서만 적용. 윈도우 합산을 쓰므로 필수는 아님.
     */
    public void decreaseHourlyScores() {
        String key = todayKey();
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        Set<Object> allBoards = zSetOps.range(key, 0, -1);
        if (allBoards == null || allBoards.isEmpty()) {
            log.info("랭킹(오늘 키)에 게시글이 없습니다.");
            return;
        }
        int decreasedCount = 0;
        int removedCount = 0;
        for (Object boardIdObj : allBoards) {
            String boardId = boardIdObj.toString();
            Double currentScore = zSetOps.score(key, boardId);
            if (currentScore != null) {
                double newScore = currentScore - 5.0;
                if (newScore < MIN_SCORE) {
                    zSetOps.remove(key, boardId);
                    removedCount++;
                    log.debug("게시글 {} 점수 {}로 오늘 랭킹에서 제거", boardId, currentScore);
                } else {
                    zSetOps.add(key, boardId, newScore);
                    decreasedCount++;
                }
            }
        }
        log.info("매시간 점수 감소(오늘 키) 완료: {} 개 감소, {} 개 제거", decreasedCount, removedCount);
    }

    /**
     * 감쇠 로직(옵션): 오늘 키에 대해 비율 감소
     */
    public void decreasePercentageScores() {
        String key = todayKey();
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        Set<Object> allBoards = zSetOps.range(key, 0, -1);
        if (allBoards == null || allBoards.isEmpty()) {
            log.info("랭킹(오늘 키)에 게시글이 없습니다.");
            return;
        }
        int decreasedCount = 0;
        int removedCount = 0;
        for (Object boardIdObj : allBoards) {
            String boardId = boardIdObj.toString();
            Double currentScore = zSetOps.score(key, boardId);
            if (currentScore != null) {
                double newScore = currentScore * 0.8; // 20% 차감
                if (newScore < MIN_SCORE) {
                    zSetOps.remove(key, boardId);
                    removedCount++;
                    log.debug("게시글 {} 점수 {}로 오늘 랭킹에서 제거", boardId, currentScore);
                } else {
                    zSetOps.add(key, boardId, newScore);
                    decreasedCount++;
                }
            }
        }
        log.info("20% 점수 감소(오늘 키) 완료: {} 개 감소, {} 개 제거", decreasedCount, removedCount);
    }

    /**
     * 전체 랭킹 초기화: 최근 WINDOW_DAYS 범위 및 롤링 키 삭제
     */
    public void clearRanking() {
        LocalDate today = LocalDate.now();
        for (int i = 0; i < WINDOW_DAYS; i++) {
            redisTemplate.delete(keyFor(today.minusDays(i)));
        }
        redisTemplate.delete(ROLLING_KEY);
        log.info("랭킹 초기화 완료(최근 {}일)", WINDOW_DAYS);
    }

    /**
     * 한 달이 넘은 일자 키를 정리(삭제)합니다. 운영 스케줄러에서 하루 1회 호출 권장.
     */
    public void purgeOldDailyKeys() {
        LocalDate cutoff = LocalDate.now().minusDays(WINDOW_DAYS); // 이 날짜 이전은 삭제 대상
        // 안전하게 365일까지 과거를 훑으면서 존재하는 키만 삭제
        int deleted = 0;
        for (int daysAgo = WINDOW_DAYS + 1; daysAgo <= 365; daysAgo++) {
            LocalDate target = LocalDate.now().minusDays(daysAgo);
            String key = keyFor(target);
            Boolean removed = redisTemplate.delete(key);
            if (Boolean.TRUE.equals(removed)) {
                deleted++;
            }
        }
        log.info("트렌딩 일자 키 정리 완료: 기준일 {} 이전 키 {}개 삭제", cutoff, deleted);
    }
}
