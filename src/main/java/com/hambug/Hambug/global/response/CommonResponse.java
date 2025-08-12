package com.hambug.Hambug.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CommonResponse<T>(boolean success, T data, String message) {

    public static <T> CommonResponse<T> ok(T data) {
        return new CommonResponse<>(true, data, "success");
    }

    public static <T> CommonResponse<T> ok(T data, String message) {
        return new CommonResponse<>(true, data, message);
    }

    public static <T> CommonResponse<T> fail(String message) {
        return new CommonResponse<>(false, null, message);
    }

}
