package fittoring.application.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OauthSignUpRequest(
        @NotBlank
        String name,
        @NotBlank
        String gender,
        @PhoneNumber
        @NotBlank
        String phone) {

}
