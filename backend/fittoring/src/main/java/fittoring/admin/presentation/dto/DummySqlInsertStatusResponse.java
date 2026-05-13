package fittoring.admin.presentation.dto;

import java.time.OffsetDateTime;

public record DummySqlInsertStatusResponse(
        int fileSeq,
        String scenarioFile,
        boolean inserted,
        OffsetDateTime appliedStartAt
) {
}
