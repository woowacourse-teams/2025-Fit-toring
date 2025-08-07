package fittoring.mentoring.business.model;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.InvalidStatusException;
import lombok.Getter;

@Getter
public enum Status {

    APPROVE("승인"),
    PENDING("대기"),
    REJECT("거절"),
    COMPLETE("완료"),
    ;

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public void validate(Status updateStatus) {
        if (this == APPROVE || this == REJECT || this == COMPLETE) {
            throw new InvalidStatusException(BusinessErrorMessage.RESERVATION_STATUS_ALREADY_UPDATE.getMessage());
        }
        if (this.equals(updateStatus)) {
            throw new InvalidStatusException(BusinessErrorMessage.RESERVATION_STATUS_ALREADY_EQUAL.getMessage());
        }
    }

    public boolean isPending() {
        return this.equals(PENDING);
    }
}
