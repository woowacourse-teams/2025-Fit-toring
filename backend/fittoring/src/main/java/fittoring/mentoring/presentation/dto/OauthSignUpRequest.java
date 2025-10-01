package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.presentation.PhoneNumber;
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
