package fittoring.admin.presentation.dto;

import java.time.LocalDate;

public record AdminReservationResponse(
    Long reservationId,
    String menteeName,
    LocalDate createdAt,
    String status,
    String content
) {

}
