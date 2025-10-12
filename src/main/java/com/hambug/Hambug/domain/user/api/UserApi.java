package com.hambug.Hambug.domain.user.api;

import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import com.hambug.Hambug.domain.user.dto.UserDto;
import com.hambug.Hambug.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import static com.hambug.Hambug.domain.user.dto.UserRequestDto.UpdateUserNicknameReqDto;

@Tag(name = "회원 API", description = "회원 정보 조회 및 수정 관련 API")
public interface UserApi {

    @Operation(summary = "회원 정보 조회 (마이페이지에서도 사용)", description = "회원 ID로 회원 정보를 조회합니다.")
    CommonResponse<UserDto> getUsers(@PathVariable("id") Long id, @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "닉네임 변경 (마이페이지에서도 사용)", description = "본인 인증을 거친 뒤 닉네임을 변경합니다.")
    CommonResponse<UserDto> updateNickName(@PathVariable("id") Long id,
                                           @RequestBody(description = "닉네임 변경 요청", required = true,
                                                   content = @Content(schema = @Schema(implementation = UpdateUserNicknameReqDto.class))) UpdateUserNicknameReqDto body,
                                           @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "프로필 이미지 변경 (마이페이지에서도 사용)", description = "멀티파트로 프로필 이미지를 업로드하여 변경합니다.")
    CommonResponse<UserDto> updateProfile(@PathVariable("id") Long id,
                                          @RequestPart(value = "file") MultipartFile file,
                                          @AuthenticationPrincipal PrincipalDetails principalDetails);
}
