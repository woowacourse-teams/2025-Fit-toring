package fittoring.application.mentoring.presentation.dto.response;

import fittoring.domain.model.Category;

public record CategoryResponse(
        Long id,
        String title
) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getTitle());
    }
}
