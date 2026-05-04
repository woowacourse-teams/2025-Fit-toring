package fittoring.application.community.dummy;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dummy.scheduler")
public record DummySchedulerProperties(
        boolean enabled,
        long pollIntervalMs,
        BatchSize batchSize,
        int maxAttempt
) {

    public record BatchSize(
            int post,
            int comment
    ) {
    }
}
