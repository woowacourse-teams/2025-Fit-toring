package fittoring.mentoring.business.model;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.InvalidPhoneException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Embeddable
public class Phone {

    @Column(name = "phoneNumber", nullable = false, unique = true)
    final private String number;

    protected Phone() {
        this.number = null;
    }

    public Phone(String phoneNumber) {
        validate(phoneNumber);
        this.number = phoneNumber;
    }

    private void validate(String number) {
        if (!number.matches("^\\d{2,3}-\\d{3,4}-\\d{4}$")) {
            throw new InvalidPhoneException(BusinessErrorMessage.PHONE_INVALID + number);
        }
    }
}
