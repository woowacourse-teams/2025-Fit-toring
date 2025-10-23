package fittoring.application.reservation.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReservationStatusUpdateRequest(@NotBlank String status) {

}
