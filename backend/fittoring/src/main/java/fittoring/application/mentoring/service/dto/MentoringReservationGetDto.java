package fittoring.application.mentoring.service.dto;

public record MentoringReservationGetDto(
        Long memberId,
        Long mentoringId,
        int page
) {

}
