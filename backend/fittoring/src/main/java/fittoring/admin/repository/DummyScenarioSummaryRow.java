package fittoring.admin.repository;

import java.time.Duration;
import java.time.OffsetDateTime;

public record DummyScenarioSummaryRow(
        long id,
        String originalFilename,
        String contentHash,
        DummyScenarioStatus status,
        OffsetDateTime uploadedAt,
        OffsetDateTime insertedAt,
        OffsetDateTime originalStartAt,
        Duration originalDuration,
        OffsetDateTime appliedStartAt,
        Duration appliedDuration,
        int postCount,
        int commentCount
) {
}
