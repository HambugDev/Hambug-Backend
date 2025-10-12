package com.hambug.Hambug.global.util;

import lombok.Setter;

/**
 * Reusable pagination parameters with sane defaults and normalization.
 * Compose this in any request DTO to avoid re-implementing pagination logic.
 */
@Setter
public class PageParams implements PageQueryUtil {

    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 100;
    private static final int DEFAULT_OFFSET = 0;
    private static final String DEFAULT_ORDER = "DESC";
    private Integer pageLimit;   // nullable in payload
    private Integer pageOffset;  // nullable in payload
    private Long lastId;         // nullable in payload
    private String sortOrder;    // ASC or DESC (case-insensitive), nullable in payload
    public PageParams() {
    }
    public PageParams(Integer pageLimit, Integer pageOffset, Long lastId, String sortOrder) {
        this.pageLimit = pageLimit;
        this.pageOffset = pageOffset;
        this.lastId = lastId;
        this.sortOrder = sortOrder;
    }

    @Override
    public int limit() {
        int l = pageLimit == null ? DEFAULT_LIMIT : pageLimit;
        if (l < 1) l = DEFAULT_LIMIT;
        if (l > MAX_LIMIT) l = MAX_LIMIT;
        return l;
    }

    @Override
    public int offset() {
        int o = pageOffset == null ? DEFAULT_OFFSET : pageOffset;
        return Math.max(o, 0);
    }

    @Override
    public Long lastId() {
        return lastId;
    }

    @Override
    public String order() {
        String ord = sortOrder == null ? DEFAULT_ORDER : sortOrder;
        return ("ASC".equalsIgnoreCase(ord)) ? "ASC" : "DESC";
    }
}
