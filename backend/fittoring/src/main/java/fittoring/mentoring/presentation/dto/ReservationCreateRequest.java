package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.presentation.PhoneNumber;

public record ReservationCreateRequest(
        String menteeName,
        @PhoneNumber String menteePhone,
        String content
) {
}
