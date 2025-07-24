package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.Mentoring;
import java.util.List;

public record MentoringCardResponse(
        long id,
        String mentorName,
        List<String> categories,
        int price,
        int career,
        String imageUrl,
        String introduction
) {

    public static MentoringCardResponse from(Mentoring mentoring, List<String> categoriesByMentoring) {
        return new MentoringCardResponse(
                mentoring.getId(),
                mentoring.getMentorName(),
                categoriesByMentoring,
                mentoring.getPrice(),
                mentoring.getCareer(),
                null,
                mentoring.getIntroduction()
        );
    }

    public static MentoringCardResponse from(Mentoring mentoring, List<String> categoriesByMentoring, Image image) {
        return new MentoringCardResponse(
                mentoring.getId(),
                mentoring.getMentorName(),
                categoriesByMentoring,
                mentoring.getPrice(),
                mentoring.getCareer(),
                image.getUrl(),
                mentoring.getIntroduction()
        );
    }
}
