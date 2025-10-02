package fittoring.application.business.service.dto;

import java.time.LocalDate;

public interface ParticipatedReservationDto {
    Long getReservationId();
    Long getMentoringId();
    String getMentorName();
    String getMentorProfileImage();
    LocalDate getReservedAt();
    String getContent();
    String getStatus();
    boolean getIsReviewed();
}
