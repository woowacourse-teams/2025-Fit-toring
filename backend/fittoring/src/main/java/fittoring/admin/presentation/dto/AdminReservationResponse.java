package fittoring.admin.presentation.dto;

import java.time.LocalDateTime;

public record AdminReservationResponse(
        Long reservationId,
        String menteeName,
        LocalDateTime createdAt,
        String status,
        String content
) {

}
