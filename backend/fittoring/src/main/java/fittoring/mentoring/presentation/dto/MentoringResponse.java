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
        String profileImageUrl,
        String introduction,
        String content
) {

    public static MentoringResponse from(Mentoring mentoring, List<String> categoryTitles) {
        return new MentoringResponse(
                mentoring.getId(),
                mentoring.getMentorName(),
                categoryTitles,
                mentoring.getPrice(),
                mentoring.getCareer(),
                null,
                mentoring.getIntroduction(),
                mentoring.getContent()
        );
    }

    public static MentoringResponse from(Mentoring mentoring, List<String> categoryTitles, Image image) {
        if(image==null){
            return from(mentoring, categoryTitles);
        }
        return new MentoringResponse(
                mentoring.getId(),
                mentoring.getMentorName(),
                categoryTitles,
                mentoring.getPrice(),
                mentoring.getCareer(),
                image.getUrl(),
                mentoring.getIntroduction(),
                mentoring.getContent()
        );
    }
}
