package fittoring.application.auth.presentation.dto.request;

import fittoring.application.presentation.PhoneNumber;

public record VerifyPhoneNumberRequest(
        @PhoneNumber
        String phone
) {

}
