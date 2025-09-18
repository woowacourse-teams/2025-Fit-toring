package fittoring.mentoring.business.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fittoring.mentoring.Cursor;
import fittoring.mentoring.business.model.Mentoring;
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

    private final JPAQueryFactory jpaQueryFactory;
    private final QMentoring mentoring;

    @Override
    public MentoringPaginationResult findMentoringsWithPagination(Cursor cursor) {
        SortKey sortKey = cursor.sortKey();

        BooleanBuilder where = new BooleanBuilder();
        BooleanExpression ex = ex(cursor);
        where.and(ex);

        List<Mentoring> rows = jpaQueryFactory.select(mentoring)
                .from(mentoring)
                .where(where)
                .orderBy(orderSpecifiers(sortKey))
                .limit(PAGE_SIZE + 1)
                .fetch();

        boolean hasNext = rows.size() > PAGE_SIZE;
        if (hasNext) {
            rows.subList(0, PAGE_SIZE);
        }

        String nextCursorCode = "";

        if (!rows.isEmpty()) {
            Mentoring lastMentoring = rows.getLast();

            if (sortKey.equals(SortKey.CREATED_AT)) {
                long nextSortValue = lastMentoring.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond();
                nextCursorCode = CursorCodec.incode(new Cursor(sortKey, "DESC", nextSortValue, lastMentoring.getId()));
            }
        }

        return new MentoringPaginationResult(rows, nextCursorCode, hasNext);
    }


    private BooleanExpression ex(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        if (cursor.sortKey().equals(SortKey.CREATED_AT)) {
            LocalDateTime cursorDateTime = Instant.ofEpochSecond(cursor.sortValue())
                    .atZone(ZoneId.of("Asia/Seoul"))
                    .toLocalDateTime();
            return mentoring.createdAt.lt(cursorDateTime)
                    .or(mentoring.createdAt.eq(cursorDateTime).and(mentoring.id.lt(cursor.id())));
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
