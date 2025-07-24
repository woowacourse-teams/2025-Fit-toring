package fittoring.mentoring.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.InvalidCategoryException;
import fittoring.mentoring.business.exception.MentoringNotFoundException;
import fittoring.mentoring.business.model.Category;
import fittoring.mentoring.business.model.CategoryMentoring;
import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.presentation.dto.MentoringSummaryResponse;
import fittoring.mentoring.presentation.dto.MentoringResponse;
import fittoring.util.DbCleaner;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
@Import({DbCleaner.class, MentoringService.class})
@DataJpaTest
class MentoringServiceTest {

    @Autowired
    private MentoringService mentoringService;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }

    @DisplayName("멘토링 요약 조회")
    @Nested
    class FindMentoringSummary {

        @DisplayName("필터링 설정이 없는 경우, 모든 멘토링의 요약 정보를 조회할 수 있다.")
        @Test
        void getAllMentoring1() {
            // given
            Mentoring mentoring1 = new Mentoring("김트레이너", "010-3378-9048", 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개");
            Mentoring mentoring2 = new Mentoring("박트레이너", "010-1234-5678", 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개");
            em.persist(mentoring1);
            em.persist(mentoring2);

            Category category1 = new Category("카테고리1");
            Category category2 = new Category("카테고리2");
            em.persist(category1);
            em.persist(category2);

            CategoryMentoring categoryMentoring1_1 = new CategoryMentoring(category1, mentoring1);
            CategoryMentoring categoryMentoring2_2 = new CategoryMentoring(category2, mentoring2);
            em.persist(categoryMentoring1_1);
            em.persist(categoryMentoring2_2);

            Image image1 = new Image("멘토링이미지1url", ImageType.MENTORING, mentoring1.getId());
            em.persist(image1);

            String categoryTitle1 = null;
            String categoryTitle2 = null;
            String categoryTitle3 = null;

            MentoringSummaryResponse expected = MentoringSummaryResponse.from(
                    MentoringResponse.from(
                    mentoring1,
                    List.of(categoryMentoring1_1.getCategoryTitle()),
                    image1)
            );

            MentoringSummaryResponse expected2 = MentoringSummaryResponse.from(
                    MentoringResponse.from(
                    mentoring2,
                    List.of(categoryMentoring2_2.getCategoryTitle())
                    )
            );

            // when
            List<MentoringSummaryResponse> actual = mentoringService.findMentoringSummaries(
                    categoryTitle1,
                    categoryTitle2,
                    categoryTitle3
            );

            // then
            assertThat(actual).containsExactly(expected, expected2);
        }

        @DisplayName("카테고리 필터링 조건을 만족하는 모든 멘토링의 요약 정보를 조회할 수 있다.")
        @Test
        void getAllMentoring2() {
            // given
            Mentoring mentoring1 = new Mentoring("김트레이너", "010-3333-9048", 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개");
            Mentoring mentoring2 = new Mentoring("박트레이너", "010-1234-5678", 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개");
            Mentoring mentoring3 = new Mentoring("이트레이너", "010-1234-5679", 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개");
            em.persist(mentoring1);
            em.persist(mentoring2);
            em.persist(mentoring3);

            Category category1 = new Category("카테고리1");
            Category category2 = new Category("카테고리2");
            Category category3 = new Category("카테고리3");
            em.persist(category1);
            em.persist(category2);
            em.persist(category3);

            CategoryMentoring categoryMentoring1_1 = new CategoryMentoring(category1, mentoring1);
            CategoryMentoring categoryMentoring2_1 = new CategoryMentoring(category2, mentoring1);
            CategoryMentoring categoryMentoring2_2 = new CategoryMentoring(category2, mentoring2);
            CategoryMentoring categoryMentoring1_3 = new CategoryMentoring(category1, mentoring3);
            CategoryMentoring categoryMentoring2_3 = new CategoryMentoring(category2, mentoring3);
            CategoryMentoring categoryMentoring3_3 = new CategoryMentoring(category3, mentoring3);
            em.persist(categoryMentoring1_1);
            em.persist(categoryMentoring2_1);
            em.persist(categoryMentoring2_2);
            em.persist(categoryMentoring1_3);
            em.persist(categoryMentoring2_3);
            em.persist(categoryMentoring3_3);

            Image image1 = new Image("멘토링이미지1url", ImageType.MENTORING, mentoring1.getId());
            Image image2 = new Image("멘토링이미지3url", ImageType.MENTORING, mentoring3.getId());
            em.persist(image1);
            em.persist(image2);

            String categoryTitle1 = category1.getTitle();
            String categoryTitle2 = category2.getTitle();
            String categoryTitle3 = null;

            MentoringSummaryResponse expected = MentoringSummaryResponse.from(
                    MentoringResponse.from(
                        mentoring1,
                        List.of(categoryMentoring1_1.getCategoryTitle(),
                        categoryMentoring2_1.getCategoryTitle()),
                        image1
                    )
            );

            MentoringSummaryResponse expected2 = MentoringSummaryResponse.from(
                    MentoringResponse.from(
                            mentoring3,
                            List.of(categoryMentoring1_3.getCategoryTitle(),
                                    categoryMentoring2_3.getCategoryTitle(),
                                    categoryMentoring3_3.getCategoryTitle()
                            ),
                            image2
                    )
            );

            // when
            List<MentoringSummaryResponse> actual = mentoringService.findMentoringSummaries(
                    categoryTitle1,
                    categoryTitle2,
                    categoryTitle3
            );

            // then
            assertThat(actual).containsExactly(expected, expected2);
        }

        @DisplayName("존재하지 않는 카테고리 이름을 필터링하려는 경우 예외가 발생한다.")
        @Test
        void getAllMentoring5() {
            // given
            Mentoring mentoring1 = new Mentoring("김트레이너", "010-3378-9048", 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개");
            Mentoring mentoring2 = new Mentoring("박트레이너", "010-1234-5678", 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개");
            em.persist(mentoring1);
            em.persist(mentoring2);

            Category category1 = new Category("카테고리1");
            Category category2 = new Category("카테고리2");
            em.persist(category1);
            em.persist(category2);

            CategoryMentoring categoryMentoring1_1 = new CategoryMentoring(category1, mentoring1);
            CategoryMentoring categoryMentoring1_2 = new CategoryMentoring(category1, mentoring2);
            CategoryMentoring categoryMentoring2_2 = new CategoryMentoring(category2, mentoring2);
            em.persist(categoryMentoring1_1);
            em.persist(categoryMentoring1_2);
            em.persist(categoryMentoring2_2);

            Image image1 = new Image("멘토링이미지1url", ImageType.MENTORING, mentoring1.getId());
            em.persist(image1);

            String categoryTitle1 = category1.getTitle();
            String categoryTitle2 = "비정상카테고리";
            String categoryTitle3 = null;

            // when
            // then
            assertThatThrownBy(() -> mentoringService.findMentoringSummaries(
                    categoryTitle1,
                    categoryTitle2,
                    categoryTitle3
            ))
                    .isInstanceOf(InvalidCategoryException.class)
                    .hasMessage(BusinessErrorMessage.INVALID_CATEGORY.getMessage());
        }

        @DisplayName("필터 조건에 해당하는 멘토링이 존재하지 않는 경우, 빈 리스트를 반환한다.")
        @Test
        void getAllMentoring6() {
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

            CategoryMentoring categoryMentoring1_1 = new CategoryMentoring(category1, mentoring1);
            CategoryMentoring categoryMentoring2_1 = new CategoryMentoring(category2, mentoring1);
            CategoryMentoring categoryMentoring2_2 = new CategoryMentoring(category2, mentoring2);
            em.persist(categoryMentoring1_1);
            em.persist(categoryMentoring2_1);
            em.persist(categoryMentoring2_2);

            Image image1 = new Image("멘토링이미지1url", ImageType.MENTORING, mentoring1.getId());
            em.persist(image1);

            String categoryTitle1 = null;
            String categoryTitle2 = null;
            String categoryTitle3 = category3.getTitle();

            // when
            List<MentoringSummaryResponse> actual = mentoringService.findMentoringSummaries(
                    categoryTitle1,
                    categoryTitle2,
                    categoryTitle3
            );

            // then
            assertThat(actual).isEmpty();
        }
    }

    @Nested
    @DisplayName("멘토링 정보 조회")
    class FindMentoring{
        @DisplayName("멘토링 id로 멘토링을 조회한다.")
        @Test
        void getMentoring() {
            //given
            Mentoring mentoring1 = new Mentoring("김트레이너", "010-3378-9048", 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개");
            em.persist(mentoring1);

            Category category1 = new Category("카테고리1");
            em.persist(category1);

            CategoryMentoring categoryMentoring1_1 = new CategoryMentoring(category1, mentoring1);
            em.persist(categoryMentoring1_1);

            Image image1 = new Image("멘토링이미지1url", ImageType.MENTORING, mentoring1.getId());
            em.persist(image1);

            MentoringResponse expected = MentoringResponse.from(
                    mentoring1,
                    List.of(category1.getTitle()),
                    image1
            );

            //when
            MentoringResponse actual = mentoringService.getMentoring(mentoring1.getId());

            //then
            assertThat(actual).isEqualTo(expected);
        }

        @DisplayName("존재하지 않는 멘토링 id로 멘토링을 조회하는 경우 예외가 발생한다.")
        @Test
        void getMentoring2() {
            //given
            Mentoring mentoring1 = new Mentoring("김트레이너", "010-3378-9048", 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개");
            em.persist(mentoring1);

            Category category1 = new Category("카테고리1");
            em.persist(category1);

            CategoryMentoring categoryMentoring1_1 = new CategoryMentoring(category1, mentoring1);
            em.persist(categoryMentoring1_1);

            Image image1 = new Image("멘토링이미지1url", ImageType.MENTORING, mentoring1.getId());
            em.persist(image1);

            Long invalidId = 100L;

            //when
            //then
            assertThatThrownBy(() ->
                    mentoringService.getMentoring(invalidId))
                    .isInstanceOf(MentoringNotFoundException.class)
                    .hasMessageStartingWith(BusinessErrorMessage.MENTORING_NOT_FOUND.getMessage());
        }
    }
}
