package fittoring.mentoring.business.repository;

import fittoring.mentoring.Cursor;
import fittoring.mentoring.business.service.dto.MentoringPaginationResult;

public interface CustomMentoringRepository {

    MentoringPaginationResult findMentoringsWithPagination(Cursor cursor);
}
