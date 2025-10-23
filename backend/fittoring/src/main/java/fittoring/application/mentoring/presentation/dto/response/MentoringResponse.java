package fittoring.application.mentoring.presentation.dto.response;

import fittoring.domain.model.Image;
import fittoring.domain.model.Mentoring;
import java.util.List;

public record MentoringResponse(
        long id,
        String mentorName,
        List<String> categories,
        int price,
        int career,
        String profileImageUrl,
        String introduction,
        String content,
        List<CertificateSpecAndImageResponse> certificates,
        String ratingAverage,
        long ratingCount
) {

    public static MentoringResponse of(
            Mentoring mentoring,
            List<String> categoryTitles,
            Image image,
            List<CertificateSpecAndImageResponse> certificates,
            double ratingAverage,
            long ratingCount
    ) {
        if (image == null) {
            return of(mentoring, categoryTitles, certificates, ratingAverage, ratingCount);
        }
        return new MentoringResponse(
                mentoring.getId(),
                mentoring.getMentorName(),
                categoryTitles,
                mentoring.getPrice(),
                mentoring.getCareer(),
                image.getUrl(),
                mentoring.getIntroduction(),
                mentoring.getContent(),
                certificates,
                String.format("%.1f", ratingAverage),
                ratingCount
        );
    }

    private static MentoringResponse of(
            Mentoring mentoring,
            List<String> categoryTitles,
            List<CertificateSpecAndImageResponse> certificates,
            double ratingAverage,
            long ratingCount
    ) {
        return new MentoringResponse(
                mentoring.getId(),
                mentoring.getMentorName(),
                categoryTitles,
                mentoring.getPrice(),
                mentoring.getCareer(),
                null,
                mentoring.getIntroduction(),
                mentoring.getContent(),
                certificates,
                String.format("%.1f", ratingAverage),
                ratingCount
        );
    }
}
