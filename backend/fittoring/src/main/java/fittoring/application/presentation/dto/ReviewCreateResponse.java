package fittoring.application.presentation.dto;

public record ReviewCreateResponse(
    Long mentoringId,
    int rating,
    String content) {

}
