package fittoring.mentoring.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.mentoring.business.model.Category;
import fittoring.mentoring.business.model.CategoryMentoring;
import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.presentation.dto.MentoringResponse;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @DisplayName("멘토링 조회")
    @Nested
    class FindMentoring {

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

            String categoryTitle1 = null;
            String categoryTitle2 = null;
            String categoryTitle3 = null;

            // when
            // then
            MentoringResponse response = MentoringResponse.from(mentoring1,
                    List.of(categoryMentoring1.getCategoryTitle()),
                    image1);
            MentoringResponse response2 = MentoringResponse.from(mentoring2,
                    List.of(categoryMentoring2.getCategoryTitle()));

            assertThat(mentoringService.getAllMentoring(
                    categoryTitle1,
                    categoryTitle2,
                    categoryTitle3)
            )
                    .containsExactly(response, response2);
        }

        @DisplayName("검색 필터가 하나인 경우 ")
        @Test
        void getAllMentoringWithSingleFilter() {
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

            String categoryTitle1 = category1.getTitle();
            String categoryTitle2 = null;
            String categoryTitle3 = null;

            // when
            // then
            MentoringResponse response = MentoringResponse.from(mentoring1,
                    List.of(categoryMentoring1.getCategoryTitle()),
                    image1);

            assertThat(mentoringService.getAllMentoring(
                    categoryTitle1,
                    categoryTitle2,
                    categoryTitle3)
            )
                    .containsExactly(response);
        }

        @DisplayName("검색 필터가 2개 이상인 경우")
        @Test
        void getAllMentoringWithSingleFilter2() {
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
            CategoryMentoring categoryMentoring2 = new CategoryMentoring(mentoring1, category2);
            CategoryMentoring categoryMentoring3 = new CategoryMentoring(mentoring2, category2);
            em.persist(categoryMentoring1);
            em.persist(categoryMentoring2);
            em.persist(categoryMentoring3);

            Image image1 = new Image("멘토링이미지1url", ImageType.MENTORING, mentoring1.getId());
            em.persist(image1);

            String categoryTitle1 = category1.getTitle();
            String categoryTitle2 = category2.getTitle();
            String categoryTitle3 = null;

            // when
            // then
            MentoringResponse response = MentoringResponse.from(mentoring1,
                    List.of(categoryMentoring1.getCategoryTitle(),
                            categoryMentoring2.getCategoryTitle()),
                    image1);

            assertThat(mentoringService.getAllMentoring(
                    categoryTitle1,
                    categoryTitle2,
                    categoryTitle3)
            )
                    .containsExactly(response);
        }

        @DisplayName("존재하지 않는 카테고리 이름을 필터링하려는 경우 예외가 발생한다.")
        @Test
        void getAllMentoringWithInvalidCategory() {
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
            CategoryMentoring categoryMentoring2 = new CategoryMentoring(mentoring1, category2);
            CategoryMentoring categoryMentoring3 = new CategoryMentoring(mentoring2, category2);
            em.persist(categoryMentoring1);
            em.persist(categoryMentoring2);
            em.persist(categoryMentoring3);

            Image image1 = new Image("멘토링이미지1url", ImageType.MENTORING, mentoring1.getId());
            em.persist(image1);

            String categoryTitle1 = category1.getTitle();
            String categoryTitle2 = "비정상카테고리";
            String categoryTitle3 = null;

            // when
            // then
            assertThatThrownBy(() ->
                    mentoringService.getAllMentoring(
                            categoryTitle1,
                            categoryTitle2,
                            categoryTitle3
                    )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("존재하지 않는 카테고리가 포함되어 있습니다.");
        }

        @DisplayName("필터 조건에 해당하는 멘토링이 존재하지 않는 경우, 빈 리스트를 반환한다.")
        @Test
        void getFilteredMentoringWithNoContent() {
            // given
            Mentoring mentoring1 = new Mentoring("김트레이너", "010-3378-9048", 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개");
            Mentoring mentoring2 = new Mentoring("박트레이너", "010-1234-5678", 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개");
            em.persist(mentoring1);
            em.persist(mentoring2);

            Category category1 = new Category("카테고리1");
            Category category2 = new Category("카테고리2");
            Category category3 = new Category("카테고리3");
            em.persist(category1);
            em.persist(category2);
            em.persist(category3);

            CategoryMentoring categoryMentoring1 = new CategoryMentoring(mentoring1, category1);
            CategoryMentoring categoryMentoring2 = new CategoryMentoring(mentoring1, category2);
            CategoryMentoring categoryMentoring3 = new CategoryMentoring(mentoring2, category2);
            em.persist(categoryMentoring1);
            em.persist(categoryMentoring2);
            em.persist(categoryMentoring3);

            Image image1 = new Image("멘토링이미지1url", ImageType.MENTORING, mentoring1.getId());
            em.persist(image1);

            String categoryTitle1 = null;
            String categoryTitle2 = null;
            String categoryTitle3 = category3.getTitle();

            // when
            // then
            assertThat(mentoringService.getAllMentoring(
                    categoryTitle1,
                    categoryTitle2,
                    categoryTitle3)
            ).isEmpty();
        }
    }
}
