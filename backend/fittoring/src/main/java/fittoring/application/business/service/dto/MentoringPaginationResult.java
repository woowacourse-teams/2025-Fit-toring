package fittoring.application.business.service.dto;

import fittoring.application.business.model.Mentoring;
import java.util.List;

public record MentoringPaginationResult(
        List<Mentoring> mentorings,
        String nextCursorCode,
        boolean hasNext
) {
}
