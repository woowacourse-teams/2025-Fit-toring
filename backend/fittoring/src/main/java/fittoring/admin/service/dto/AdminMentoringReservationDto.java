package fittoring.admin.service.dto;

public record AdminMentoringReservationDto(
        Long memberId,
        Long mentoringId,
        int page,
        int size
) {
}
