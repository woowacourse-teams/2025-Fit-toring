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
    private static final QCategoryMentoring categoryMentoring = QCategoryMentoring.categoryMentoring;

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public MentoringPaginationResult findMentoringsWithPagination(SortKey sortKey, Cursor cursor,
                                                                  List<Long> categoryIds) {
        BooleanBuilder where = new BooleanBuilder();
        BooleanExpression cursorCondition = buildCursorCondition(sortKey, cursor);
        BooleanExpression categoryFilterCondition = buildCategoryFilterCondition(categoryIds);

        where.and(cursorCondition)
                .and(categoryFilterCondition);

        List<Mentoring> rows = jpaQueryFactory.select(mentoring)
                .from(mentoring)
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
                // 다른 정렬 기준이 추가될 수 있습니다.
            };
        }
        return new MentoringPaginationResult(rows, nextCursorCode, hasNext);
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

    private String getNextCursorCode(Mentoring nextMentoring) {
        String nextCursorCode;
        long nextSortValue = nextMentoring.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond();
        nextCursorCode = CursorCodec.encode(new Cursor(nextSortValue, nextMentoring.getId()));
        return nextCursorCode;
    }

    private BooleanExpression buildCursorCondition(SortKey sortKey, Cursor cursor) {
        if (sortKey.equals(SortKey.CREATED_AT) && cursor != null) {
            LocalDateTime cursorDateTime = Instant.ofEpochSecond(cursor.sortValue())
                    .atZone(ZoneId.of("Asia/Seoul"))
                    .toLocalDateTime();
            return mentoring.createdAt.lt(cursorDateTime)
                    .or(
                            mentoring.createdAt.eq(cursorDateTime)
                                    .and(mentoring.id.loe(cursor.id()))
                    );

        }
        return null;
    }

    private OrderSpecifier<?>[] orderSpecifiers(SortKey sortKey) {
        return switch (sortKey) {
            case CREATED_AT -> new OrderSpecifier<?>[]{
                    mentoring.createdAt.desc(),
                    mentoring.id.desc()
            };
        };
    }
}
