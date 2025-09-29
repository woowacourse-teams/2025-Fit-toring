package fittoring.mentoring.business.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fittoring.mentoring.Cursor;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.QCategoryMentoring;
import fittoring.mentoring.business.model.QMentoring;
import fittoring.mentoring.business.model.QMentoringStatistics;
import fittoring.mentoring.business.model.SortKey;
import fittoring.mentoring.business.service.dto.MentoringPaginationResult;
import fittoring.util.CursorCodec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomMentoringRepositoryImpl implements CustomMentoringRepository {

    private static final int PAGE_SIZE = 10;
    private static final QMentoring mentoring = QMentoring.mentoring;
    private static final QMentoringStatistics mentoringStatistics = QMentoringStatistics.mentoringStatistics;
    private static final QCategoryMentoring categoryMentoring = QCategoryMentoring.categoryMentoring;

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public MentoringPaginationResult findMentoringsWithPagination(
        SortKey sortKey,
        Cursor cursor,
        List<Long> categoryIds
    ) {
        BooleanBuilder where = buildWhereClause(sortKey, cursor, categoryIds);

        List<Mentoring> rows = jpaQueryFactory.select(mentoring)
                .from(mentoring)
                .join(mentoringStatistics)
                .on(mentoringStatistics.mentoring.eq(mentoring)
                .and(mentoringStatistics.isDeleted.isFalse()))
                .where(where)
                .orderBy(orderSpecifiers(sortKey))
                .limit(PAGE_SIZE + 1)
                .fetch();

        boolean hasNext = rows.size() > PAGE_SIZE;
        String nextCursorCode = null;
        if (hasNext) {
            Mentoring nextMentoring = rows.getLast();
            rows = rows.subList(0, PAGE_SIZE);
            nextCursorCode = switch (sortKey) {
                case CREATED_AT -> getNextCursorCode(nextMentoring);
                case RESERVATION_COUNT ->
            };
        }
        return new MentoringPaginationResult(rows, nextCursorCode, hasNext);
    }

    private BooleanBuilder buildWhereClause(SortKey sortKey, Cursor cursor, List<Long> categoryIds) {
        BooleanBuilder where = new BooleanBuilder();
        where.and(buildCursorCondition(sortKey, cursor))
            .and(buildCategoryFilterCondition(categoryIds))
            .and(buildSoftDeleteCondition());
        return where;
    }

    private BooleanExpression buildCursorCondition(SortKey sortKey, Cursor cursor) {
        if (sortKey == SortKey.CREATED_AT && cursor != null) {
            LocalDateTime cursorDateTime = Instant.ofEpochSecond(cursor.sortValue())
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();
            return mentoring.createdAt.lt(cursorDateTime)
                .or(
                    mentoring.createdAt.eq(cursorDateTime)
                        .and(mentoring.id.loe(cursor.id()))
                );

        }
        if (sortKey == SortKey.RESERVATION_COUNT && cursor != null) {
            long cursorReservationCount = cursor.sortValue();
            return mentoringStatistics.reservationCount.lt(cursorReservationCount)
                .or(
                    mentoringStatistics.reservationCount.eq(cursorReservationCount)
                        .and(mentoring.id.loe(cursor.id()))
                );
        }
        return null;
    }

    private BooleanExpression buildCategoryFilterCondition(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return null;
        }
        NumberExpression<Long> distinctCnt = categoryMentoring.category.id.countDistinct();

        return mentoring.id.in(
                JPAExpressions
                        .select(categoryMentoring.mentoring.id)
                        .from(categoryMentoring)
                        .where(
                                categoryMentoring.isDeleted.isFalse(),
                                categoryMentoring.category.id.in(categoryIds)
                        )
                        .groupBy(categoryMentoring.mentoring.id)
                        .having(distinctCnt.eq((long) categoryIds.size()))
        );
    }

    private BooleanExpression buildSoftDeleteCondition() {
        return mentoring.isDeleted.isFalse();
    }

    private String getNextCursorCode(Mentoring nextMentoring) {
        String nextCursorCode;
        long nextSortValue = nextMentoring.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond();
        nextCursorCode = CursorCodec.encode(new Cursor(nextSortValue, nextMentoring.getId()));
        return nextCursorCode;
    }

    private OrderSpecifier<?>[] orderSpecifiers(SortKey sortKey) {
        return switch (sortKey) {
            case CREATED_AT -> new OrderSpecifier<?>[]{
                    mentoring.createdAt.desc(),
                    mentoring.id.desc()
            };
            case RESERVATION_COUNT -> new OrderSpecifier<?>[]{
                    mentoringStatistics.reservationCount.desc(),
                    mentoring.id.desc()
            };
        };
    }
}
