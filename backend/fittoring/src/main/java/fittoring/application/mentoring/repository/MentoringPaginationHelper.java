package fittoring.application.mentoring.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import fittoring.util.Cursor;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.MentoringStatistics;
import fittoring.domain.model.QCategoryMentoring;
import fittoring.domain.model.QMentoring;
import fittoring.domain.model.QMentoringStatistics;
import fittoring.domain.model.SortKey;
import fittoring.util.CursorCodec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MentoringPaginationHelper {

    private static final QMentoring mentoring = QMentoring.mentoring;
    private static final QMentoringStatistics mentoringStatistics = QMentoringStatistics.mentoringStatistics;
    private static final QCategoryMentoring categoryMentoring = QCategoryMentoring.categoryMentoring;

    /**
     * 멘토링 페이지네이션 조회에 사용되는 where절을 생성한다.
     *
     * @param sortKey 페이지네이션 조회 시 정렬 기준
     * @param cursor 현재 커서의 위치
     * @param categoryIds 필터링에 사용할 카테고리 id
     * @return 생성한 where절
     */
    public Predicate buildWhereClause(SortKey sortKey, Cursor cursor, List<Long> categoryIds) {
        return ExpressionUtils.allOf(
            buildSoftDeleteCondition(),
            buildCursorCondition(sortKey, cursor),
            buildCategoryFilterCondition(categoryIds)
        );
    }

    /**
     * 소프트 삭제 완료된 행은 포함되지 않도록 하는 조건절을 추가한다.
     */
    private BooleanExpression buildSoftDeleteCondition() {
        return mentoring.isDeleted.isFalse();
    }

    /**
     * 현재 커서의 위치에서 다음 값들을 가져오는 조건을 추가한다.
     */
    private BooleanExpression buildCursorCondition(SortKey sortKey, Cursor cursor) {
        if (sortKey == SortKey.CREATED_AT && cursor != null) {
            LocalDateTime cursorDateTime = Instant.ofEpochSecond(cursor.sortValue())
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();
            return mentoring.createdAt.lt(cursorDateTime)
                .or(mentoring.createdAt.eq(cursorDateTime)
                        .and(mentoring.id.loe(cursor.id())));
        }
        if (sortKey == SortKey.RESERVATION_COUNT && cursor != null) {
            long cursorReservationCount = cursor.sortValue();
            return mentoringStatistics.reservationCount.lt(cursorReservationCount)
                .or(mentoringStatistics.reservationCount.eq(cursorReservationCount)
                        .and(mentoring.id.loe(cursor.id())));
        }
        return null;
    }

    /**
     * 보여질 카테고리만 필터링하는 조건절을 추가한다.
     */
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

    /**
     * 멘토링 페이지네이션 조회에 사용되는 orderBy절을 생성한다.
     *
     * @param sortKey 정렬 기준이 될 행
     * @return 생성한 orderBy절
     */
    public OrderSpecifier<?>[] buildOrderSpecifiers(SortKey sortKey) {
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

    /**
     * 다음 페이지에 값이 존재할 경우, 다음 페이지 조회를 위해 커서를 생성하여 문자열로 변환한다.
     *
     * @param sortKey 페이지네이션 조회 시 정렬 기준
     * @param hasNext 다음 페이지에 값 존재 여부
     * @param rows 현재 페이지 조회 결과 튜플 (Mentoring, MentoringStatics)
     * @return 커서를 문자열으로 변환한 값
     */
    public String generateNextCursorCode(SortKey sortKey, boolean hasNext, List<Tuple> rows) {
        if (hasNext) {
            Tuple nextTuple = rows.getLast();
            Mentoring nextMentoring = nextTuple.get(mentoring);
            MentoringStatistics nextMentoringStatistics = nextTuple.get(mentoringStatistics);
            return switch (sortKey) {
                case CREATED_AT -> getNextCursorCodeOfCreatedAt(nextMentoring);
                case RESERVATION_COUNT -> getNextCursorCodeOfReservationCount(nextMentoringStatistics.getReservationCount(), nextMentoring.getId());
            };
        }
        return null;
    }

    /**
     * 정렬 기준이 created_at인 경우의 다음 커서를 문자열한다.
     * LocalDateTime 타입의 created_at을 long 타입으로 변환하여 Cursor 객체를 만든 후 문자열화 한다.
     */
    private String getNextCursorCodeOfCreatedAt(Mentoring nextMentoring) {
        String nextCursorCode;
        long nextSortValue = nextMentoring.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond();
        nextCursorCode = CursorCodec.encode(new Cursor(nextSortValue, nextMentoring.getId()));
        return nextCursorCode;
    }

    /**
     * 정렬 기준이 reservation_count인 경우의 다음 커서를 문자열한다.
     * 다음 멘토링 값의 reservation_count를 사용하여 Cursor 객체를 만든 후 문자열화 한다.
     */
    private String getNextCursorCodeOfReservationCount(long nextReservationCount, long nextMentoringId) {
        return CursorCodec.encode(new Cursor(nextReservationCount, nextMentoringId));
    }
}
