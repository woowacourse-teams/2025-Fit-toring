package fittoring.admin.presentation.dto;

import java.time.Duration;
import java.time.OffsetDateTime;

public record DummySqlInsertResponse(
        long scenarioId,
        String originalFilename,
        int insertedScenarioCount,
        int insertedPostPendingCount,
        int insertedCommentPendingCount,
        String status,
        OffsetDateTime appliedStartAt,
        Duration appliedDuration
) {
}
