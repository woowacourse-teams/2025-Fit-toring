package fittoring.application.business.service.dto;

import fittoring.application.presentation.dto.MentoringSummaryResponse;
import java.util.List;

public record MentoringSummaryPaginationResponse(
        List<MentoringSummaryResponse> mentoringSummaryResponses,
        String nextCursorCode,
        boolean hasNext
) {
}
