package com.hambug.Hambug.global.notification.api;

import com.hambug.Hambug.global.notification.dto.RegisterFcmTokenRequest;
import com.hambug.Hambug.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "FCM 토큰 API", description = "FCM 디바이스 토큰 등록/삭제 API")
public interface FcmTokenApi {

    @Operation(summary = "FCM 토큰 등록/갱신", description = "사용자의 FCM 디바이스 토큰을 등록하거나 갱신합니다.")
    CommonResponse<Boolean> register(@RequestBody RegisterFcmTokenRequest request,
                                     @Parameter(hidden = true) Authentication authentication);

}
