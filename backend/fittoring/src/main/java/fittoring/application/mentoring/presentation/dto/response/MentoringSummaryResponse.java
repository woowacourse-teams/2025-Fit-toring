package fittoring.application.mentoring.presentation.dto.response;

import fittoring.domain.model.Image;
import fittoring.domain.model.Mentoring;
import fittoring.application.mentoring.service.dto.RatingStatsDto;
import java.util.List;

public record MentoringSummaryResponse(
        long id,
        String mentorName,
        List<String> categories,
        int price,
        int career,
        String profileImageUrl,
        String introduction,
        String ratingAverage,
        long ratingCount
) {

    public static MentoringSummaryResponse of(
        Mentoring mentoring,
        List<String> categories,
        Image image,
        RatingStatsDto ratingStatsDto
    ) {
        if (image == null) {
            return MentoringSummaryResponse.of(mentoring, categories, ratingStatsDto);
        }
        return new MentoringSummaryResponse(
                mentoring.getId(),
                mentoring.getMentorName(),
                categories,
                mentoring.getPrice(),
                mentoring.getCareer(),
                image.getUrl(),
                mentoring.getIntroduction(),
                String.format("%.1f", ratingStatsDto.average()),
                ratingStatsDto.count()
        );
    }

    private static MentoringSummaryResponse of(
        Mentoring mentoring,
        List<String> categories,
        RatingStatsDto ratingStatsDto
    ) {
        return new MentoringSummaryResponse(
                mentoring.getId(),
                mentoring.getMentorName(),
                categories,
                mentoring.getPrice(),
                mentoring.getCareer(),
                null,
                mentoring.getIntroduction(),
                String.format("%.1f", ratingStatsDto.average()),
                ratingStatsDto.count()
        );
    }
}
