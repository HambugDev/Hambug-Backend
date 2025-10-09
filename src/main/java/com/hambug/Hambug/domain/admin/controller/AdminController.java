package com.hambug.Hambug.domain.admin.controller;

import com.hambug.Hambug.domain.admin.api.AdminApi;
import com.hambug.Hambug.domain.admin.dto.AdminUserReqDTO;
import com.hambug.Hambug.domain.admin.dto.AdminUserResponseDto;
import com.hambug.Hambug.domain.admin.service.AdminUserService;
import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import com.hambug.Hambug.global.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AdminController implements AdminApi {

    private final AdminUserService userService;

    @PostMapping("/register")
    public AdminUserResponseDto.AdminUserResponse createAdminUser(@RequestBody RegisterAdminUser body) {
        return userService.createAdminUser(body);
    }

    @PostMapping("/register/manager")
    public AdminUserResponseDto.AdminUserResponse createManagerUser(@RequestBody RegisterAdminUser body, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return userService.createManagerUser(body);
    }

    @PostMapping("/login")
    public AdminUserResponseDto.AdminLoginResponse login(@RequestBody AdminLogin body) {
        return userService.login(body);
    }

    @PostMapping("/send-email")
    public CommonResponse<AdminUserResponseDto.AdminValidSendEmailResponse> sendEmail(@RequestBody AdminUserReqDTO.AdminValidSendEmail boy) {
        return CommonResponse.ok(userService.sendEmail(boy));
    }

    @PostMapping("/verification-email")
    public CommonResponse<Boolean> verificationEmail(@RequestBody AdminUserReqDTO.VerificationEmail body) {
        userService.verificationEmail(body);
        return CommonResponse.ok(true);
    }

    @PostMapping("/password/reset")
    public CommonResponse<Boolean> resetPassword(@RequestBody AdminUserReqDTO.ResetPassword body) {
        boolean resetPassword = userService.resetPassword(body);
        return CommonResponse.ok(resetPassword);
    }

}
