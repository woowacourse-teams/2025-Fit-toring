package fittoring.application.mentoring.repository;

import fittoring.admin.presentation.dto.AdminMentoringResponse;
import fittoring.application.mentoring.service.dto.MentoringPaginationResult;
import fittoring.domain.model.SortKey;
import fittoring.util.Cursor;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomMentoringRepository {

    MentoringPaginationResult findMentoringsWithPagination(SortKey sortKey, Cursor cursor, List<Long> categoryIds);

    Page<AdminMentoringResponse> findAllWithPagination(Pageable pageable);
}
