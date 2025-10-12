package com.hambug.Hambug.domain.burger.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hambug.Hambug.domain.burger.dto.BurgerRequestDTO;
import com.hambug.Hambug.domain.burger.dto.BurgerResponseDTO;
import com.hambug.Hambug.domain.burger.entity.Burger;
import com.hambug.Hambug.domain.burger.repository.BurgerRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BurgerService {

    private final BurgerRepository burgerRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String RECOMMENDED_BURGERS_KEY = "burger:recommended:today";
    private static final String BURGER_IDS_KEY = "burger:recommended:ids";

    @PostConstruct
    @Transactional
    public void loadBurgerData() {
        try {
            long count = burgerRepository.count();
            if (count > 0) {
                log.info("햄버거 데이터가 이미 존재합니다. ({}개)", count);
                return;
            }

            ClassPathResource resource = new ClassPathResource("buger.json");
            InputStream inputStream = resource.getInputStream();

            List<Map<String, String>> burgerDataList = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<Map<String, String>>>() {}
            );

            List<Burger> burgers = burgerDataList.stream()
                    .map(data -> Burger.builder()
                            .menuImage(data.get("menu_image"))
                            .franchise(data.get("franchise"))
                            .menuName(data.get("menu_name"))
                            .menuDescription(data.get("menu_description"))
                            .build())
                    .collect(Collectors.toList());

            burgerRepository.saveAll(burgers);
            log.info("햄버거 데이터 {}개 로드 완료", burgers.size());

        } catch (IOException e) {
            log.error("햄버거 데이터 로드 실패", e);
            throw new RuntimeException("햄버거 데이터 로드에 실패했습니다.", e);
        }
    }

    @Transactional(readOnly = true)
    public void selectTodayRecommendedBurgers() {
        List<Burger> randomBurgers = burgerRepository.findRandomBurgers(3);

        if (randomBurgers.isEmpty()) {
            log.warn("햄버거 데이터가 없습니다.");
            return;
        }

        List<Long> burgerIds = randomBurgers.stream()
                .map(Burger::getId)
                .collect(Collectors.toList());

        redisTemplate.opsForValue().set(BURGER_IDS_KEY, burgerIds, 24, TimeUnit.HOURS);

        log.info("오늘의 추천 햄버거 선택 완료: {}", burgerIds);
    }

    @Transactional(readOnly = true)
    public List<BurgerResponseDTO.BurgerInfo> getTodayRecommendedBurgers() {
        Object cachedIds = redisTemplate.opsForValue().get(BURGER_IDS_KEY);

        List<Long> burgerIds;

        if (cachedIds == null) {
            log.info("캐시된 추천 햄버거가 없습니다. 새로 선택합니다.");
            selectTodayRecommendedBurgers();
            cachedIds = redisTemplate.opsForValue().get(BURGER_IDS_KEY);

            if (cachedIds == null) {
                log.error("추천 햄버거 선택 실패");
                return List.of();
            }
        }

        burgerIds = ((List<?>) cachedIds).stream()
                .map(id -> Long.parseLong(id.toString()))
                .collect(Collectors.toList());

        List<Burger> burgers = burgerRepository.findAllById(burgerIds);

        Map<Long, Burger> burgerMap = burgers.stream()
                .collect(Collectors.toMap(Burger::getId, burger -> burger));

        return burgerIds.stream()
                .map(burgerMap::get)
                .filter(burger -> burger != null)
                .map(BurgerResponseDTO.BurgerInfo::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BurgerResponseDTO.BurgerInfo> getAllBurgers() {
        List<Burger> burgers = burgerRepository.findAll();
        return BurgerResponseDTO.BurgerInfo.fromList(burgers);
    }

    @Transactional
    public BurgerResponseDTO.BurgerInfo createBurger(BurgerRequestDTO.BurgerCreateRequest request) {
        Burger burger = Burger.builder()
                .menuImage(request.getMenuImage())
                .franchise(request.getFranchise())
                .menuName(request.getMenuName())
                .menuDescription(request.getMenuDescription())
                .build();

        Burger savedBurger = burgerRepository.save(burger);
        log.info("햄버거 생성 완료: {}", savedBurger.getId());
        return BurgerResponseDTO.BurgerInfo.from(savedBurger);
    }

    @Transactional
    public BurgerResponseDTO.BurgerInfo updateBurger(Long id, BurgerRequestDTO.BurgerUpdateRequest request) {
        Burger burger = burgerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("햄버거를 찾을 수 없습니다."));

        Burger updatedBurger = Burger.builder()
                .id(burger.getId())
                .menuImage(request.getMenuImage())
                .franchise(request.getFranchise())
                .menuName(request.getMenuName())
                .menuDescription(request.getMenuDescription())
                .build();

        Burger savedBurger = burgerRepository.save(updatedBurger);
        log.info("햄버거 수정 완료: {}", savedBurger.getId());
        return BurgerResponseDTO.BurgerInfo.from(savedBurger);
    }

    @Transactional
    public void deleteBurger(Long id) {
        Burger burger = burgerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("햄버거를 찾을 수 없습니다."));

        burgerRepository.delete(burger);
        log.info("햄버거 삭제 완료: {}", id);
    }
}
