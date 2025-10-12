package com.hambug.Hambug.domain.burger.controller;

import com.hambug.Hambug.domain.burger.api.BurgerApi;
import com.hambug.Hambug.domain.burger.dto.BurgerRequestDTO;
import com.hambug.Hambug.domain.burger.dto.BurgerResponseDTO;
import com.hambug.Hambug.domain.burger.service.BurgerService;
import com.hambug.Hambug.global.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/burgers")
public class BurgerController implements BurgerApi {

    private final BurgerService burgerService;

    @GetMapping("/recommended")
    @Override
    public CommonResponse<List<BurgerResponseDTO.BurgerInfo>> getRecommendedBurgers() {
        List<BurgerResponseDTO.BurgerInfo> burgers = burgerService.getTodayRecommendedBurgers();
        return CommonResponse.ok(burgers);
    }

    @GetMapping
    @Override
    public CommonResponse<List<BurgerResponseDTO.BurgerInfo>> getAllBurgers() {
        List<BurgerResponseDTO.BurgerInfo> burgers = burgerService.getAllBurgers();
        return CommonResponse.ok(burgers);
    }

    @PostMapping
    @Override
    public CommonResponse<BurgerResponseDTO.BurgerInfo> createBurger(@RequestBody BurgerRequestDTO.BurgerCreateRequest request) {
        BurgerResponseDTO.BurgerInfo burger = burgerService.createBurger(request);
        return CommonResponse.ok(burger);
    }

    @PutMapping("/{id}")
    @Override
    public CommonResponse<BurgerResponseDTO.BurgerInfo> updateBurger(@PathVariable("id") Long id, @RequestBody BurgerRequestDTO.BurgerUpdateRequest request) {
        BurgerResponseDTO.BurgerInfo burger = burgerService.updateBurger(id, request);
        return CommonResponse.ok(burger);
    }

    @DeleteMapping("/{id}")
    @Override
    public CommonResponse<Boolean> deleteBurger(@PathVariable("id") Long id) {
        burgerService.deleteBurger(id);
        return CommonResponse.ok(true);
    }
}
