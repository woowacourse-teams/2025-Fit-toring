package fittoring.mentoring.business.model;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.InvalidPhoneException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Phone {

    @Column(nullable = false)
    private String number;

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
