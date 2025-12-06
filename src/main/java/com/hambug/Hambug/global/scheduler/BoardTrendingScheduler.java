package com.hambug.Hambug.global.scheduler;

import com.hambug.Hambug.domain.board.service.trending.BoardTrendingService;
import com.hambug.Hambug.domain.burger.service.BurgerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BoardTrendingScheduler {

    private final BoardTrendingService boardTrendingService;
    private final BurgerService burgerService;


    @Scheduled(cron = "0 0 * * * *")
    public void decreaseHourlyScores() {
        log.info("=== 매시간 점수 감소 스케줄러 실행 ===");
        boardTrendingService.decreaseHourlyScores();
    }


    @Scheduled(cron = "0 0 0,8,16 * * *")
    public void decreasePercentageScores() {
        log.info("=== 20% 점수 감소 스케줄러 실행 ===");
        boardTrendingService.decreasePercentageScores();
    }


    @Scheduled(cron = "0 0 0 * * *")
    public void selectDailyRecommendedBurgers() {
        log.info("=== 오늘의 추천 햄버거 선택 스케줄러 실행 ===");
        burgerService.selectTodayRecommendedBurgers();
    }
}
