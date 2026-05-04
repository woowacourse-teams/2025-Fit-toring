package fittoring.application.community.dummy.scenario;

import java.time.OffsetDateTime;

public record ScenarioPost(
        String nickname,
        OffsetDateTime scheduledAt,
        String title,
        String content
) {
}
