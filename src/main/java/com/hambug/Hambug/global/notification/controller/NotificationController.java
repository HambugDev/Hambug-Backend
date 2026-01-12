package com.hambug.Hambug.global.notification.controller;

import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import com.hambug.Hambug.global.notification.api.NotificationApi;
import com.hambug.Hambug.global.notification.dto.NotificationResponseDTO;
import com.hambug.Hambug.global.notification.service.NotificationService;
import com.hambug.Hambug.global.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController implements NotificationApi {

    private final NotificationService notificationService;

    @GetMapping
    public CommonResponse<NotificationResponseDTO.NotificationAllResponse> getNotifications(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "20") int limit
    ) {
        Long userId = principalDetails.getUserDto().getUserId();
        NotificationResponseDTO.NotificationAllResponse response = notificationService.getNotifications(userId, lastId, limit);
        return CommonResponse.ok(response);
    }
}
