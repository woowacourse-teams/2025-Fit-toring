package fittoring.mentoring.business.service.dto;

import fittoring.mentoring.presentation.dto.MentoringSummaryResponse;
import java.util.List;

public record MentoringSummaryPaginationResponse(
        List<MentoringSummaryResponse> mentoringSummaryResponses,
        String nextCursorCode,
        boolean hasNext
) {
}
