package fittoring.mentoring.business.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fittoring.mentoring.Cursor;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.QMentoring;
import fittoring.mentoring.business.model.QMentoringStatistics;
import fittoring.mentoring.business.model.SortKey;
import fittoring.mentoring.business.repository.helper.MentoringPaginationHelper;
import fittoring.mentoring.business.service.dto.MentoringPaginationResult;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomMentoringRepositoryImpl implements CustomMentoringRepository {

    private static final int PAGE_SIZE = 10;
    private static final QMentoring mentoring = QMentoring.mentoring;
    private static final QMentoringStatistics mentoringStatistics = QMentoringStatistics.mentoringStatistics;

    private final JPAQueryFactory jpaQueryFactory;
    private final MentoringPaginationHelper mentoringPaginationHelper;

    @Override
    public MentoringPaginationResult findMentoringsWithPagination(
        SortKey sortKey,
        Cursor cursor,
        List<Long> categoryIds
    ) {
        // 결과 행을 튜플 타입(Mentoring, MentoringStatistics)으로 가져온다.
        List<Tuple> mentoringAndMentoringStatistics = jpaQueryFactory
                .select(mentoring, mentoringStatistics)
                .from(mentoring)
                    .leftJoin(mentoringStatistics)
                    .on(mentoringStatistics.mentoringId.eq(mentoring.id))
                .where(mentoringPaginationHelper.buildWhereClause(sortKey, cursor, categoryIds))
                .orderBy(mentoringPaginationHelper.buildOrderSpecifiers(sortKey))
                .limit(PAGE_SIZE + 1)
                .fetch();

        // 다음 행이 존재할 경우 다음 커서를 문자열화 해서 반환한다.
        boolean hasNext = mentoringAndMentoringStatistics.size() > PAGE_SIZE;
        String nextCursorCode = mentoringPaginationHelper.generateNextCursorCode(sortKey, hasNext, mentoringAndMentoringStatistics);

        // 결과 행을 튜플 타입에서 List<Mentoring> 타입으로 변환한 후 반환한다.
        List<Mentoring> mentorings = mapTuplesToMentorings(mentoringAndMentoringStatistics);
        return new MentoringPaginationResult(mentorings, nextCursorCode, hasNext);
    }

    private List<Mentoring> mapTuplesToMentorings(List<Tuple> rows) {
        rows = rows.subList(0, Math.min(rows.size(), PAGE_SIZE));
        return rows.stream()
            .map(t -> t.get(mentoring))
            .toList();
    }
}
