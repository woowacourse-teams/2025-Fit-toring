package fittoring.application.review.presentation.dto.response;

public record ReviewCreateResponse(
        Long mentoringId,
        int rating,
        String content) {

}
