package fittoring.application.service.dto;

public record RatingStatsDto(
    Long mentoringId,
    double average,
    long count
) {

}
