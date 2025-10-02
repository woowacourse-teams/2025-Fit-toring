package fittoring.mentoring.business.service.dto;

public record RatingStatsDto(
    Long mentoringId,
    double average,
    long count
) {

}
