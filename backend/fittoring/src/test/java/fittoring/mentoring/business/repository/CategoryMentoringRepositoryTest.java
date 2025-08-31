package fittoring.mentoring.business.repository;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import fittoring.config.JpaConfiguration;
import fittoring.mentoring.business.model.Category;
import fittoring.mentoring.business.model.CategoryMentoring;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.password.Password;
import fittoring.util.DbCleaner;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
@Import({DbCleaner.class, JpaConfiguration.class})
@DataJpaTest
class CategoryMentoringRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMentoringRepository categoryMentoringRepository;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }

    @DisplayName("카테고리_멘토링을 삭제하면 삭제가 일어난 시간과 함께 삭제상태로 변경된다.")
    @Test
    void softDelete() {
        //given
        Member savedMentor = memberRepository.save(
                new Member("id1", "MALE", "김트레이너", new Phone("010-1234-9048"), Password.from("pw"))
        );

        Mentoring savedMentoring = mentoringRepository.save(
                new Mentoring(savedMentor, 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개")
        );

        Category savedCategory = categoryRepository.save(new Category("체형교정"));
        Category savedCategory2 = categoryRepository.save(new Category("근육증진"));

        CategoryMentoring categoryMentoring = categoryMentoringRepository.save(
                new CategoryMentoring(savedCategory, savedMentoring)
        );
        CategoryMentoring savedCategoryMentoring2 = categoryMentoringRepository.save(
                new CategoryMentoring(savedCategory2, savedMentoring)
        );

        LocalDateTime beforeDelete = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        //when
        categoryMentoringRepository.delete(categoryMentoring);

        em.flush();
        em.clear();
        LocalDateTime afterDelete = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        //then
        List<CategoryMentoring> deletedCategoryMentoring = categoryMentoringRepository.findAllDeleted();
        CategoryMentoring actual = deletedCategoryMentoring.getFirst();

        assertSoftly(softly -> {
            softly.assertThat(actual.isDeleted()).isTrue();
            softly.assertThat(actual.getDeletedAt())
                    .isBeforeOrEqualTo(afterDelete)
                    .isAfterOrEqualTo(beforeDelete);
        });
    }
}
