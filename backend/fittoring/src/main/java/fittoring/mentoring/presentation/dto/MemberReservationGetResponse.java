package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.business.model.Status;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record MemberReservationGetResponse(
    String mentorName,
    String mentorProfileImage,
    int price,
    LocalDate reservedAt,
    List<String> categories,
    boolean isReviewed
) {

}
