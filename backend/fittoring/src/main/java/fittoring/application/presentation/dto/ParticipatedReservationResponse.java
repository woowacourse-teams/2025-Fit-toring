package fittoring.application.presentation.dto;

import java.time.LocalDate;

public record ParticipatedReservationResponse(
        Long reservationId,
        Long mentoringId,
        String mentorName,
        String mentorProfileImage,
        LocalDate reservedAt,
        String content,
        String status,
        boolean isReviewed
) {

}
