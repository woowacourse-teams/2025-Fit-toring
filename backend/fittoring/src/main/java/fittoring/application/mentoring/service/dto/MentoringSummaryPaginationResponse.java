package fittoring.application.mentoring.service.dto;

import fittoring.application.mentoring.presentation.dto.response.MentoringSummaryResponse;
import java.util.List;

public record MentoringSummaryPaginationResponse(
        List<MentoringSummaryResponse> mentoringSummaryResponses,
        String nextCursorCode,
        boolean hasNext
) {
}
