package fittoring.application.community.dummy.scenario;

import java.time.OffsetDateTime;
import java.util.List;

public record ScenarioComment(
        String nickname,
        OffsetDateTime scheduledAt,
        String content,
        List<ScenarioComment> replies
) {
}
