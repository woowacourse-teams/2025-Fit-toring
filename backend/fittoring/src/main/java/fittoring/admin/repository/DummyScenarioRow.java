package fittoring.admin.repository;

import java.time.Duration;
import java.time.OffsetDateTime;

public record DummyScenarioRow(
        long id,
        String originalFilename,
        String contentHash,
        String yamlContent,
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
