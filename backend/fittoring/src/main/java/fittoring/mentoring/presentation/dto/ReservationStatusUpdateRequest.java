package fittoring.mentoring.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record ReservationStatusUpdateRequest(
        @NotBlank String status
) {
}
