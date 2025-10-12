package com.hambug.Hambug.domain.mypage.controller;

import com.hambug.Hambug.domain.mypage.dto.MyPageRequestDto;
import com.hambug.Hambug.domain.mypage.service.MyPageService;
import com.hambug.Hambug.domain.oauth.entity.PrincipalDetails;
import com.hambug.Hambug.global.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/my-pages")
@RequiredArgsConstructor
@Slf4j
public class MyPageController {

    private final MyPageService myPageService;

    private static Long getUserId(PrincipalDetails principalDetails) {
        return principalDetails.getUser().getUserId();
    }

    @GetMapping("/boards")
    public CommonResponse<?> myBoards(@ModelAttribute MyPageRequestDto.MyBoardRequest query,
                                      @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long userId = getUserId(principalDetails);
        return CommonResponse.ok(myPageService.getMyBoards(query, userId));
    }

    @GetMapping("/comments")
    public CommonResponse<?> myComments(@ModelAttribute MyPageRequestDto.MyCommentRequest query,
                                        @AuthenticationPrincipal PrincipalDetails principalDetails) {
        myPageService.getMyComments();
        return CommonResponse.ok(true);
    }
}
