package fittoring.mentoring.business.service;

import fittoring.mentoring.business.model.Category;
import fittoring.mentoring.business.model.CategoryMentoring;
import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.presentation.dto.MentoringResponse;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(MentoringService.class)
@DataJpaTest
class MentoringServiceTest {

    @Autowired
    private MentoringService mentoringService;

    @Autowired
    private EntityManager em;

    @DisplayName("모든 멘토링 조회를 할 수 있다.")
    @Test
    void getAllMentoring() {
        // given
        Mentoring mentoring1 = new Mentoring("김트레이너", "010-3378-9048", 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개");
        Mentoring mentoring2 = new Mentoring("박트레이너", "010-1234-5678", 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개");
        em.persist(mentoring1);
        em.persist(mentoring2);

        Category category1 = new Category("카테고리1");
        Category category2 = new Category("카테고리2");
        em.persist(category1);
        em.persist(category2);

        CategoryMentoring categoryMentoring1 = new CategoryMentoring(mentoring1, category1);
        CategoryMentoring categoryMentoring2 = new CategoryMentoring(mentoring2, category2);
        em.persist(categoryMentoring1);
        em.persist(categoryMentoring2);

        Image image1 = new Image("멘토링이미지1url", ImageType.MENTORING, mentoring1.getId());

        em.persist(image1);

        // when
        // then
        MentoringResponse response = MentoringResponse.from(mentoring1, List.of(categoryMentoring1.getCategoryTitle()), image1);
        MentoringResponse response2 = MentoringResponse.from(mentoring2, List.of(categoryMentoring2.getCategoryTitle()));

        Assertions.assertThat(mentoringService.getAllMentoring())
                .containsExactly(response, response2);

    }

}
