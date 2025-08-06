package fittoring.mentoring.presentation.dto;

import java.time.LocalDate;
import java.util.List;

public record MemberReservationGetResponse(
    Long reservationId,
    Long mentoringId,
    String mentorName,
    String mentorProfileImage,
    int price,
    LocalDate reservedAt,
    List<String> categories,
    boolean isReviewed
) {

}
