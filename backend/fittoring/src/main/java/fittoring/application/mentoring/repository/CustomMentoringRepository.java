package fittoring.application.mentoring.repository;

import fittoring.util.Cursor;
import fittoring.domain.model.SortKey;
import fittoring.application.mentoring.service.dto.MentoringPaginationResult;
import java.util.List;

public interface CustomMentoringRepository {

    MentoringPaginationResult findMentoringsWithPagination(SortKey sortKey, Cursor cursor, List<Long> categoryIds);
}
