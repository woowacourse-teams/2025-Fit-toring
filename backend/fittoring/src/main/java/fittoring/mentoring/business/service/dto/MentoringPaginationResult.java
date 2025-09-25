package fittoring.mentoring.business.service.dto;

import fittoring.mentoring.business.model.Mentoring;
import java.util.List;

public record MentoringPaginationResult(
        List<Mentoring> mentorings,
        String nextCursorCode,
        boolean hasNext
) {
}
