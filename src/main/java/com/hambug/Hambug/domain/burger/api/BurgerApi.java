package com.hambug.Hambug.domain.burger.api;

import com.hambug.Hambug.domain.burger.dto.BurgerRequestDTO;
import com.hambug.Hambug.domain.burger.dto.BurgerResponseDTO;
import com.hambug.Hambug.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Burger", description = "햄버거 추천 API")
public interface BurgerApi {

    @Operation(summary = "오늘의 추천 햄버거 조회",
               description = "매일 00시에 랜덤으로 선택된 추천 햄버거 3개를 조회합니다")
    CommonResponse<List<BurgerResponseDTO.BurgerInfo>> getRecommendedBurgers();

    @Operation(summary = "전체 햄버거 조회",
               description = "등록된 모든 햄버거 목록을 조회합니다")
    CommonResponse<List<BurgerResponseDTO.BurgerInfo>> getAllBurgers();

    @Operation(summary = "햄버거 생성",
               description = "새로운 햄버거를 등록합니다")
    CommonResponse<BurgerResponseDTO.BurgerInfo> createBurger(@RequestBody BurgerRequestDTO.BurgerCreateRequest request);

    @Operation(summary = "햄버거 수정",
               description = "기존 햄버거 정보를 수정합니다")
    CommonResponse<BurgerResponseDTO.BurgerInfo> updateBurger(@PathVariable("id") Long id, @RequestBody BurgerRequestDTO.BurgerUpdateRequest request);

    @Operation(summary = "햄버거 삭제",
               description = "햄버거를 삭제합니다")
    CommonResponse<Boolean> deleteBurger(@PathVariable("id") Long id);
}
