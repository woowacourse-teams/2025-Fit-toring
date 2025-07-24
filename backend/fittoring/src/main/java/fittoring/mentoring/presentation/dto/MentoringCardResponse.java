package fittoring.mentoring.presentation.dto;

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

    public static MentoringCardResponse from(MentoringResponse mentoringResponse) {
        return new MentoringCardResponse(
                mentoringResponse.id(),
                mentoringResponse.mentorName(),
                mentoringResponse.categories(),
                mentoringResponse.price(),
                mentoringResponse.career(),
                mentoringResponse.imageUrl(),
                mentoringResponse.introduction()
        );
    }
}
