package fittoring.admin.presentation.dto;

import fittoring.domain.model.Status;
import java.time.LocalDateTime;

public record AdminReservationResponse(
        Long reservationId,
        String menteeName,
        LocalDateTime createdAt,
        Status status,
        String content
) {

}
