package com.hambug.Hambug.domain.admin.controller;

import com.hambug.Hambug.domain.admin.service.AdminUserService;
import com.hambug.Hambug.domain.auth.dto.JwtTokenDto;
import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.hambug.Hambug.domain.admin.dto.AdminUserReqDTO.AdminLogin;
import static com.hambug.Hambug.domain.admin.dto.AdminUserReqDTO.RegisterAdminUser;

@RequestMapping("/admin/api/v1")
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminUserService userService;

    @PostMapping("/register")
    public String createAdminUser(@RequestBody RegisterAdminUser body) {
        return userService.createAdminUser(body);
    }

    @PostMapping("/register/manager")
    public String createManagerUser(@RequestBody RegisterAdminUser body, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return userService.createManagerUser(body);
    }

    @PostMapping("/login")
    public JwtTokenDto login(@RequestBody AdminLogin body) {
        return userService.login(body);
    }

}
