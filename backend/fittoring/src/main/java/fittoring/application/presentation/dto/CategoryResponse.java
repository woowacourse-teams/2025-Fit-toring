package fittoring.application.presentation.dto;

import fittoring.domain.model.Category;

public record CategoryResponse(
    Long id,
    String title
) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getTitle());
    }
}
