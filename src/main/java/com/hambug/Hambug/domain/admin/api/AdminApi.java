package com.hambug.Hambug.domain.admin.api;

import com.hambug.Hambug.domain.admin.dto.AdminUserReqDTO;
import com.hambug.Hambug.domain.admin.dto.AdminUserResponseDto;
import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import com.hambug.Hambug.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import static com.hambug.Hambug.domain.admin.dto.AdminUserReqDTO.AdminLogin;
import static com.hambug.Hambug.domain.admin.dto.AdminUserReqDTO.RegisterAdminUser;

@Tag(name = "관리자 API", description = "관리자 계정 생성, 로그인, 이메일 인증 및 비밀번호 재설정 API")
public interface AdminApi {

    @Operation(summary = "관리자 계정 생성", description = "슈퍼관리자 계정을 생성합니다.")
    AdminUserResponseDto.AdminUserResponse createAdminUser(
            @RequestBody(description = "관리자 생성 요청", required = true,
                    content = @Content(schema = @Schema(implementation = AdminUserReqDTO.RegisterAdminUser.class)))
            @org.springframework.web.bind.annotation.RequestBody RegisterAdminUser body);

    @Operation(summary = "매니저 계정 생성", description = "관리자가 매니저 계정을 생성합니다.")
    AdminUserResponseDto.AdminUserResponse createManagerUser(
            @RequestBody(description = "매니저 생성 요청", required = true,
                    content = @Content(schema = @Schema(implementation = AdminUserReqDTO.RegisterAdminUser.class)))
            @org.springframework.web.bind.annotation.RequestBody RegisterAdminUser body,
            @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "관리자 로그인", description = "관리자 로그인을 수행하고 액세스/리프레시 토큰을 반환합니다.")
    AdminUserResponseDto.AdminLoginResponse login(
            @RequestBody(description = "관리자 로그인 요청", required = true,
                    content = @Content(schema = @Schema(implementation = AdminUserReqDTO.AdminLogin.class)))
            @org.springframework.web.bind.annotation.RequestBody AdminLogin body);

    @Operation(summary = "이메일 인증코드 전송", description = "관리자 이메일로 인증 코드를 전송합니다.")
    CommonResponse<AdminUserResponseDto.AdminValidSendEmailResponse> sendEmail(
            @RequestBody(description = "이메일 전송 요청", required = true,
                    content = @Content(schema = @Schema(implementation = AdminUserReqDTO.AdminValidSendEmail.class)))
            @org.springframework.web.bind.annotation.RequestBody AdminUserReqDTO.AdminValidSendEmail body);

    @Operation(summary = "이메일 인증 검증", description = "전송된 인증 코드가 유효한지 검증합니다.")
    CommonResponse<Boolean> verificationEmail(
            @RequestBody(description = "이메일 인증 검증 요청", required = true,
                    content = @Content(schema = @Schema(implementation = AdminUserReqDTO.VerificationEmail.class)))
            @org.springframework.web.bind.annotation.RequestBody AdminUserReqDTO.VerificationEmail body);

    @Operation(summary = "관리자 비밀번호 재설정", description = "관리자 비밀번호를 재설정합니다. 임시 비밀번호 상태가 아니어야 합니다.")
    CommonResponse<Boolean> resetPassword(
            @RequestBody(description = "비밀번호 재설정 요청", required = true,
                    content = @Content(schema = @Schema(implementation = AdminUserReqDTO.ResetPassword.class)))
            @org.springframework.web.bind.annotation.RequestBody AdminUserReqDTO.ResetPassword body);
}
