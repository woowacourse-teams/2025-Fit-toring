package fittoring.admin.presentation.dto;

import java.time.Duration;
import java.util.List;

public record DummyScenarioPreviewResponse(
        long scenarioId,
        String originalFilename,
        Duration originalDuration,
        List<PostPreview> posts
) {
}
