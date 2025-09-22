package fittoring.mentoring.business.service.dto;

import java.time.LocalDate;

public interface ParticipatedReservationView {
    Long getReservationId();
    Long getMentoringId();
    String getMentorName();
    String getMentorProfileImage();
    LocalDate getReservedAt();
    String getContent();
    String getStatus();
    boolean getIsReviewed();
}
