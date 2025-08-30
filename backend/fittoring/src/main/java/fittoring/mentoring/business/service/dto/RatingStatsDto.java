package fittoring.mentoring.business.service.dto;

public record RatingStatsDto(Long mentoringId, double average, long count) {

    private static final double DEFAULT_AVERAGE = 0.0;
    private static final long DEFAULT_COUNT = 0;

    public static RatingStatsDto defaultOf(Long mentoringId) {
        return new RatingStatsDto(mentoringId, DEFAULT_AVERAGE, DEFAULT_COUNT);
    }
}
