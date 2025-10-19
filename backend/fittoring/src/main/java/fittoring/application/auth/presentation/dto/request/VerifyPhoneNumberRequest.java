package fittoring.application.auth.presentation.dto.request;

public record VerifyPhoneNumberRequest(
        @PhoneNumber
        String phone
) {

}
