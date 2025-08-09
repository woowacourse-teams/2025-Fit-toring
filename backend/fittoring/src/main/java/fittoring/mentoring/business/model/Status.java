package fittoring.mentoring.business.model;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.InvalidStatusException;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Status {

    APPROVED("승인"),
    PENDING("대기"),
    REJECTED("거절"),
    COMPLETE("완료"),
    ;

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public static Status of(String updateStatus) {
        return Arrays.stream(values())
                .filter(status -> status.name().equalsIgnoreCase(updateStatus))
                .findFirst()
                .orElseThrow(() -> new InvalidStatusException(
                                BusinessErrorMessage.STATUS_NOT_FOUND.getMessage()
                        )
                );
    }

    public void validate(Status updateStatus) {
        if (this == APPROVED || this == REJECTED || this == COMPLETE) {
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
        return this.equals(APPROVED);
    }

    public boolean isReject() {
        return this.equals(REJECTED);
    }

    public boolean isPending() {
        return this.equals(PENDING);
    }
}
