package com.hambug.Hambug.global.notification.api;

import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import com.hambug.Hambug.global.notification.dto.NotificationResponseDTO;
import com.hambug.Hambug.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "알림 API", description = "알림 관련 API 입니다.")
public interface NotificationApi {

    @Operation(summary = "알림 목록 조회", description = "사용자의 목록을 조회합니다.")
    CommonResponse<NotificationResponseDTO.NotificationAllResponse> getNotifications(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "20") int limit
    );
}
