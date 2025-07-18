package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.Mentoring;
import java.util.List;

public record MentoringResponse(
        long id,
        String mentorName,
        List<String> categories,
        int price,
        int career,
        String imageUrl,
        String introduction
) {

    public static MentoringResponse from(Mentoring mentoring, List<String> categoriesByMentoring) {
        return new MentoringResponse(
                mentoring.getId(),
                mentoring.getMentorName(),
                categoriesByMentoring,
                mentoring.getPrice(),
                mentoring.getCareer(),
                null,
                mentoring.getIntroduction()
        );
    }
    public static MentoringResponse from(Mentoring mentoring, List<String> categoriesByMentoring, Image image) {
        return new MentoringResponse(
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
