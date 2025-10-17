package fittoring.domain.model;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.InvalidStatusException;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Status {

    APPROVED,
    PENDING,
    REJECTED,
    COMPLETE,
    ;

    public static Status of(String updateStatus) {
        return Arrays.stream(values())
                .filter(status -> status.name().equalsIgnoreCase(updateStatus))
                .findFirst()
                .orElseThrow(() -> new InvalidStatusException(
                                BusinessErrorMessage.STATUS_NOT_FOUND.getMessage()
                        )
                );
    }

    public boolean isNotifiable() {
        return this.isApprove() || this.isReject();
    }

    public boolean isApprove() {
        return this == APPROVED;
    }

    public boolean isReject() {
        return this == REJECTED;
    }

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isComplete() {
        return this == COMPLETE;
    }
}
