package fittoring.application.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ValidateDuplicateLoginIdRequest(
        @NotBlank
        String loginId
) {

}
