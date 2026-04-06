package fittoring.application.community.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GuestPasswordRequest(
        @NotBlank String guestPassword
) {
}
