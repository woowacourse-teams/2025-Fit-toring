package fittoring.admin.presentation.dto;

import java.time.Duration;
import java.time.OffsetDateTime;

public record DummySqlInsertResponse(
        int fileSeq,
        String scenarioFile,
        int insertedScenarioCount,
        int insertedPostPendingCount,
        int insertedCommentPendingCount,
        String status,
        OffsetDateTime appliedStartAt,
        Duration appliedDuration
) {
}
