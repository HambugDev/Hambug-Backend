package com.hambug.Hambug.global.util;

public interface PageQueryUtil {
    int limit();

    int offset();

    Long lastId();

    String order();
}
