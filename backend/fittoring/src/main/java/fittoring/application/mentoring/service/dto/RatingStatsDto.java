package fittoring.application.mentoring.service.dto;

public record RatingStatsDto(
        Long mentoringId,
        double average,
        long count
) {

}
