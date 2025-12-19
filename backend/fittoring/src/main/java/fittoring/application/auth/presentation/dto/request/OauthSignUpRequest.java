package fittoring.application.auth.presentation.dto.request;

import fittoring.domain.model.Gender;
import jakarta.validation.constraints.NotBlank;

public record OauthSignUpRequest(
        @NotBlank
        String name,
        @NotBlank
        Gender gender,
        @PhoneNumber
        @NotBlank
        String phone) {

}
