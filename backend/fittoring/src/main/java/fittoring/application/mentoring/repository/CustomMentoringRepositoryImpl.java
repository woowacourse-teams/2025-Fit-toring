package fittoring.application.mentoring.repository;

import static com.querydsl.core.group.GroupBy.groupBy;

import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fittoring.admin.presentation.dto.AdminMentoringResponse;
import fittoring.application.mentoring.service.dto.MentoringPaginationResult;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.QCategoryMentoring;
import fittoring.domain.model.QMentoring;
import fittoring.domain.model.QMentoringStatistics;
import fittoring.domain.model.SortKey;
import fittoring.util.Cursor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class CustomMentoringRepositoryImpl implements CustomMentoringRepository {

    private static final int PAGE_SIZE = 10;
    private static final QMentoring mentoring = QMentoring.mentoring;
    private static final QMentoringStatistics mentoringStatistics = QMentoringStatistics.mentoringStatistics;
    private static final QCategoryMentoring categoryMentoring = QCategoryMentoring.categoryMentoring;

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
        String nextCursorCode = mentoringPaginationHelper.generateNextCursorCode(
                sortKey,
                hasNext,
                mentoringAndMentoringStatistics
        );

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

    @Override
    public Page<AdminMentoringResponse> findAllWithPagination(Pageable pageable) {
        List<AdminMentoringResponse> content = getAdminMentoringResponses(pageable);
        Long totalCount = getTotalCount();
        return new PageImpl<>(content, pageable, totalCount);
    }

    private List<AdminMentoringResponse> getAdminMentoringResponses(Pageable pageable) {
        List<Long> ids = jpaQueryFactory
                .select(mentoring.id)
                .from(mentoring)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(mentoring.createdAt.desc())
                .fetch();

        return jpaQueryFactory
                .from(mentoring)
                .join(mentoring.mentor)
                .join(categoryMentoring)
                .on(categoryMentoring.mentoring.id.eq(mentoring.id))
                .join(categoryMentoring.category)
                .where(mentoring.id.in(ids))
                .orderBy(mentoring.createdAt.desc())
                .transform(
                        groupBy(mentoring.id)
                                .list(
                                        Projections.constructor(
                                                AdminMentoringResponse.class,
                                                mentoring.id,
                                                mentoring.mentor.name,
                                                GroupBy.list(categoryMentoring.category.title),
                                                mentoring.price
                                        )
                                )
                );
    }

    private Long getTotalCount() {
        return jpaQueryFactory
                .select(mentoring.countDistinct())
                .from(mentoring)
                .join(categoryMentoring)
                .on(categoryMentoring.mentoring.id.eq(mentoring.id))
                .fetchOne();
    }
}
