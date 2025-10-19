package fittoring.application.mentoring.service;

import fittoring.IntegrationTestSupport;
import fittoring.application.mentoring.presentation.dto.response.CategoryResponse;
import fittoring.application.mentoring.repository.CategoryRepository;
import fittoring.domain.model.Category;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CategoryServiceTest extends IntegrationTestSupport {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @DisplayName("카테고리 전체 조회가 성공하면 모든 카테고리 목록을 반환한다.")
    @Test
    void getAllCategories() {
        // given
        Category category1 = new Category("체형교정");
        Category category2 = new Category("근육증가");
        Category category3 = new Category("다이어트");

        categoryRepository.saveAll(List.of(category1, category2, category3));

        // when
        // then
        Assertions.assertThat(categoryService.getAllCategories())
                .containsExactlyInAnyOrder(
                        new CategoryResponse(category1.getId(), category1.getTitle()),
                        new CategoryResponse(category2.getId(), category2.getTitle()),
                        new CategoryResponse(category3.getId(), category3.getTitle())
                );
    }
}
