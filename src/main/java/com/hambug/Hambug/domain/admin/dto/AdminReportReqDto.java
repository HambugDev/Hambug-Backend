package com.hambug.Hambug.domain.admin.dto;

import com.hambug.Hambug.global.util.PageParams;
import jakarta.validation.constraints.NotNull;

public class AdminReportReqDto {

    public record AdminReportPageRequest(
            Integer type,
            PageParams params,
            Long lastId,
            Integer pageLimit,
            Integer pageOffset,
            String sortOrder,
            Integer limit,
            Integer offset,
            String order
    ) {
        public PageParams effectiveParams() {
            Integer effectiveLimit = (pageLimit != null) ? pageLimit : limit;
            Integer effectiveOffset = (pageOffset != null) ? pageOffset : offset;
            String effectiveOrder = (sortOrder != null) ? sortOrder : order;

            if (params == null) {
                return new PageParams(effectiveLimit, effectiveOffset, lastId, effectiveOrder);
            }
            if (lastId != null) params.setLastId(lastId);
            if (effectiveLimit != null) params.setPageLimit(effectiveLimit);
            if (effectiveOffset != null) params.setPageOffset(effectiveOffset);
            if (effectiveOrder != null) params.setSortOrder(effectiveOrder);
            return params;
        }
    }

    public record AdminReportRequest(
            Long id,
            PageParams params,
            Long lastId,
            Integer pageLimit,
            Integer pageOffset,
            String sortOrder,
            Integer limit,
            Integer offset,
            String order
    ) {
        public PageParams effectiveParams() {
            Integer effectiveLimit = (pageLimit != null) ? pageLimit : limit;
            Integer effectiveOffset = (pageOffset != null) ? pageOffset : offset;
            String effectiveOrder = (sortOrder != null) ? sortOrder : order;

            if (params == null) {
                return new PageParams(effectiveLimit, effectiveOffset, lastId, effectiveOrder);
            }
            if (lastId != null) params.setLastId(lastId);
            if (effectiveLimit != null) params.setPageLimit(effectiveLimit);
            if (effectiveOffset != null) params.setPageOffset(effectiveOffset);
            if (effectiveOrder != null) params.setSortOrder(effectiveOrder);
            return params;
        }
    }

    public record AdminReportDelete(
            @NotNull(message = "타입을 입력해주세요")
            Integer type
    ) {
    }
}
