package fittoring.application.business.repository;

import fittoring.application.Cursor;
import fittoring.application.business.model.SortKey;
import fittoring.application.business.service.dto.MentoringPaginationResult;
import java.util.List;

public interface CustomMentoringRepository {

    MentoringPaginationResult findMentoringsWithPagination(SortKey sortKey, Cursor cursor, List<Long> categoryIds);
}
