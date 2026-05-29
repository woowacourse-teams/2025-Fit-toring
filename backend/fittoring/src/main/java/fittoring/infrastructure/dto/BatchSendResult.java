package fittoring.infrastructure.dto;

import java.util.Set;

public record BatchSendResult(Set<Long> failedOutboxIds) {

    public boolean isFailed(Long outboxId) {
        return failedOutboxIds.contains(outboxId);
    }

    public static BatchSendResult of(Set<Long> failedOutboxIds) {
        return new BatchSendResult(Set.copyOf(failedOutboxIds));
    }
}
