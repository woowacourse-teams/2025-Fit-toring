package fittoring.application.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record ValidateDuplicateLoginIdRequest(
        @NotBlank
        String loginId
) {

}
