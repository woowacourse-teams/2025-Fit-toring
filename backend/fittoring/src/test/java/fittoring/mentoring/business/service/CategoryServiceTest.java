package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.Category;
import fittoring.mentoring.presentation.dto.CategoryGetResponse;
import fittoring.util.DbCleaner;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({DbCleaner.class, CategoryService.class})
@DataJpaTest
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }

    @DisplayName("카테고리 전체 조회가 성공하면 모든 카테고리 목록을 반환한다.")
    @Test
    void getAllCategories() {
        // given
        Category category1 = testEntityManager.persist(new Category("체형교정"));
        Category category2 = testEntityManager.persist(new Category("근육증가"));
        Category category3 = testEntityManager.persist(new Category("다이어트"));

        // when
        // then
        Assertions.assertThat(categoryService.getAllCategories())
            .containsExactlyInAnyOrder(
                new CategoryGetResponse(category1.getId(), category1.getTitle()),
                new CategoryGetResponse(category2.getId(), category2.getTitle()),
                new CategoryGetResponse(category3.getId(), category3.getTitle())
            );
    }
}