package fittoring.admin.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminReservationStatusUpdateRequest(@NotBlank String status) {
}
