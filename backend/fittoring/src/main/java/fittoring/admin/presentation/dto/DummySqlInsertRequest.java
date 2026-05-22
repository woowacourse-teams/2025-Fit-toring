package fittoring.admin.presentation.dto;

import java.time.Duration;
import java.time.OffsetDateTime;

public record DummySqlInsertRequest(
        OffsetDateTime startAt,
        Duration duration
) {
}
