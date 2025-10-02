package fittoring.application.business.model;

import fittoring.application.business.exception.BusinessErrorMessage;
import fittoring.application.business.exception.InvalidStatusException;
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

    public void validateReservation(Status updateStatus) {
        if (this == REJECTED || this == COMPLETE) {
            throw new InvalidStatusException(BusinessErrorMessage.RESERVATION_STATUS_ALREADY_UPDATE.getMessage());
        }
        if (this.equals(updateStatus)) {
            throw new InvalidStatusException(BusinessErrorMessage.RESERVATION_STATUS_ALREADY_EQUAL.getMessage());
        }
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
