package fittoring.application.mentoring.service.dto;

import fittoring.domain.model.Mentoring;
import java.util.List;

public record MentoringPaginationResult(
        List<Mentoring> mentorings,
        String nextCursorCode,
        boolean hasNext
) {
}
