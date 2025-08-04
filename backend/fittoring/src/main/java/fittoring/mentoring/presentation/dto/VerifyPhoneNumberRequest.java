package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.presentation.PhoneNumber;

public record VerifyPhoneNumberRequest(
        @PhoneNumber String phone
) {
}
