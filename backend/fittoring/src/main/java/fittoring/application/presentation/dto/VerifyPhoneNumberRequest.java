package fittoring.application.presentation.dto;

import fittoring.application.presentation.PhoneNumber;

public record VerifyPhoneNumberRequest(
        @PhoneNumber
        String phone
) {

}
