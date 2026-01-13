package com.hambug.Hambug.domain.report.repository;

import com.hambug.Hambug.domain.admin.dto.AdminReportResponseDto;
import com.hambug.Hambug.domain.report.entity.QReport;
import com.hambug.Hambug.domain.report.entity.TargetType;
import com.hambug.Hambug.domain.user.entity.QUser;
import com.hambug.Hambug.global.util.PageParams;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.hambug.Hambug.domain.admin.dto.AdminReportResponseDto.ReportGroupSummary;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CustomReportRepositoryImpl implements CustomReportRepository {

    private final JPAQueryFactory factory;

    @Override
    public Slice<ReportGroupSummary> fetchGroupedSliceByTargetType(TargetType targetType, PageParams params) {
        QReport r = QReport.report;

        PaginationContext page = getPaginationContext(params);

        BooleanBuilder where = new BooleanBuilder();
        if (targetType != null) {
            where.and(r.targetType.eq(targetType));
        }

        NumberExpression<Long> maxId = r.id.max();

        BooleanBuilder having = new BooleanBuilder();
        if (page.lastId != null) {
            if (page.isAsc()) {
                having.and(maxId.gt(page.lastId));
            } else {
                having.and(maxId.lt(page.lastId));
            }
        }

        List<Tuple> tuples = factory
                .select(r.targetId, r.targetType, maxId, r.id.count())
                .from(r)
                .where(where)
                .groupBy(r.targetId, r.targetType)
                .having(having)
                .orderBy(page.orderSpec(maxId))
                .offset(page.offset)
                .limit(page.limitPlusOne)
                .fetch();

        boolean hasNext = tuples.size() > page.limit;
        List<Tuple> contentTuples = hasNext ? tuples.subList(0, page.limit) : tuples;

        List<ReportGroupSummary> content = contentTuples.stream()
                .map(t -> new ReportGroupSummary(
                        t.get(r.targetId),
                        t.get(r.targetType),
                        t.get(maxId),
                        t.get(r.id.count())
                ))
                .collect(Collectors.toList());

        return new SliceImpl<>(content, PageRequest.of(0, page.limit), hasNext);
    }

    @Override
    public Slice<AdminReportResponseDto.ReportDetail> getReportSlice(Long targetId, PageParams params) {
        QReport r = QReport.report;
        QUser u = QUser.user;
        PaginationContext page = getPaginationContext(params);

        BooleanBuilder where = new BooleanBuilder(r.targetId.eq(targetId));

        if (page.lastId != null) {
            if (page.isAsc()) {
                where.and(r.id.gt(page.lastId));
            } else {
                where.and(r.id.lt(page.lastId));
            }
        }

        List<Tuple> results = factory
                .select(r.id, r.userId, u.nickname, r.title, r.reason, r.createdAt)
                .from(r)
                .join(u).on(r.userId.eq(u.id))
                .where(where)
                .orderBy(page.orderSpec(r.id))
                .offset(page.offset)
                .limit(page.limitPlusOne)
                .fetch();

        boolean hasNext = results.size() > page.limit;
        List<Tuple> content = hasNext ? results.subList(0, page.limit) : results;
        List<AdminReportResponseDto.ReportDetail> details = content.stream()
                .map(t -> new AdminReportResponseDto.ReportDetail(
                        t.get(r.id),
                        t.get(r.userId),
                        t.get(u.nickname),
                        t.get(r.title),
                        t.get(r.reason),
                        t.get(r.createdAt)
                ))
                .toList();

        return new SliceImpl<>(details, PageRequest.of(0, page.limit), hasNext);
    }

    private PaginationContext getPaginationContext(PageParams params) {
        Long lastId = params != null ? params.lastId() : null;
        String order = params != null ? params.order() : "DESC";
        int limit = params != null ? params.limit() : 20;
        int offset = params != null ? params.offset() : 0;
        int limitPlusOne = limit + 1;
        return new PaginationContext(lastId, order, limit, offset, limitPlusOne);
    }

    private record PaginationContext(
            Long lastId,
            String order,
            int limit,
            int offset,
            int limitPlusOne
    ) {
        boolean isAsc() {
            return "ASC".equalsIgnoreCase(order);
        }

        OrderSpecifier<Long> orderSpec(NumberExpression<Long> column) {
            return isAsc() ? column.asc() : column.desc();
        }

        OrderSpecifier<Long> orderSpec(NumberPath<Long> column) {
            return isAsc() ? column.asc() : column.desc();
        }
    }
}
