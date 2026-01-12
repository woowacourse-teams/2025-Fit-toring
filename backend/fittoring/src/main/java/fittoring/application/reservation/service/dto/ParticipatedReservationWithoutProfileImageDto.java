package fittoring.application.reservation.service.dto;

import java.time.LocalDate;

public interface ParticipatedReservationWithoutProfileImageDto {
    Long getReservationId();

    Long getMentoringId();

    String getMentorName();

    LocalDate getReservedAt();

    String getContent();

    String getStatus();

    boolean getIsReviewed();
}
