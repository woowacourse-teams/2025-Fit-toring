package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.business.model.Category;

public record CategoryGetResponse(Long id, String title) {

    public static CategoryGetResponse from(Category category) {
        return new CategoryGetResponse(category.getId(), category.getTitle());
    }
}
