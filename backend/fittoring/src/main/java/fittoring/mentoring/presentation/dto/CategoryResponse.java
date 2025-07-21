package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.business.model.Category;

public record CategoryResponse(Long id, String title) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getTitle());
    }
}
