package fittoring.application.repository;

import fittoring.application.Cursor;
import fittoring.domain.model.SortKey;
import fittoring.application.service.dto.MentoringPaginationResult;
import java.util.List;

public interface CustomMentoringRepository {

    MentoringPaginationResult findMentoringsWithPagination(SortKey sortKey, Cursor cursor, List<Long> categoryIds);
}
