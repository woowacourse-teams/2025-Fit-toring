package fittoring.admin.presentation.dto;

import java.time.Duration;
import java.time.OffsetDateTime;

public record DummySqlInsertStatusResponse(
        long scenarioId,
        String originalFilename,
        String status,
        OffsetDateTime uploadedAt,
        OffsetDateTime insertedAt,
        OffsetDateTime appliedStartAt,
        Duration originalDuration,
        Duration appliedDuration,
        int postCount,
        int commentCount
) {
}
