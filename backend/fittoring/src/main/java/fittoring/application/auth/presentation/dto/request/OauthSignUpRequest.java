package fittoring.application.auth.presentation.dto.request;

import fittoring.domain.model.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OauthSignUpRequest(
        @NotBlank
        String name,
        @NotNull
        Gender gender,
        @PhoneNumber
        @NotBlank
        String phoneNumber
) {

}
