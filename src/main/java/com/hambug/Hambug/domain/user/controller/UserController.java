package com.hambug.Hambug.domain.user.controller;

import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import com.hambug.Hambug.domain.user.dto.UserDto;
import com.hambug.Hambug.domain.user.service.UserService;
import com.hambug.Hambug.global.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.hambug.Hambug.domain.user.dto.UserRequestDto.UpdateUserNicknameReqDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public CommonResponse<?> getUsers(@PathVariable("id") Long id) {
        UserDto userDto = userService.getById(id);
        return CommonResponse.ok(userDto);
    }

    @PutMapping("/{id}")
    public CommonResponse<?> updateNickName(@PathVariable("id") Long id,
                                            @RequestBody UpdateUserNicknameReqDto body,
                                            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long userId = getUserId(principalDetails);
        UserDto userDto = userService.updateNickname(id, userId, body.nickname());
        return CommonResponse.ok(userDto);
    }

    private Long getUserId(PrincipalDetails principalDetails) {
        return principalDetails.getUser().getUserId();
    }
}
