package fittoring.mentoring.presentation.dto;

public record ReviewCreateResponse(
    Long mentoringId,
    int rating,
    String content
) {

}
