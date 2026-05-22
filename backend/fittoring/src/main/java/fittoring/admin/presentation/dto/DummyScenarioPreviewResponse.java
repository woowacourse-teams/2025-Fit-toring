package fittoring.admin.presentation.dto;

import java.time.Duration;
import java.util.List;

public record DummyScenarioPreviewResponse(
        int fileSeq,
        String scenarioFile,
        Duration originalDuration,
        List<PostPreview> posts
) {
}
