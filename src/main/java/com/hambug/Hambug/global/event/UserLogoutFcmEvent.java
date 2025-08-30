package com.hambug.Hambug.global.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserLogoutFcmEvent {
    private final Long userId;
}
