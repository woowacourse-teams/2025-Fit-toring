package fittoring.mentoring.business.repository;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import fittoring.mentoring.business.model.Category;
import fittoring.mentoring.business.model.CategoryMentoring;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.business.repository.helper.MentoringPaginationHelper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

@Import({MentoringPaginationHelper.class})
class CategoryMentoringRepositoryTest extends RepositoryTestSupport {

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

    @DisplayName("카테고리_멘토링을 삭제하면 삭제가 일어난 시간과 함께 삭제상태로 변경된다.")
    @Test
    void softDelete() {
        //given
        Member savedMentor = memberRepository.save(
                new Member("id1", "MALE", "김트레이너", new Phone("010-1234-9048"), Password.from("pw"))
        );

        Mentoring savedMentoring = mentoringRepository.save(
                new Mentoring(savedMentor, 5000, 3, "컨텐츠컨텐츠", "자기소개자기소개", "가상의오픈채팅링크")
        );

        Category savedCategory = categoryRepository.save(new Category("체형교정"));
        Category savedCategory2 = categoryRepository.save(new Category("근육증진"));

        CategoryMentoring categoryMentoring = categoryMentoringRepository.save(
                new CategoryMentoring(savedCategory, savedMentoring)
        );
        CategoryMentoring savedCategoryMentoring2 = categoryMentoringRepository.save(
                new CategoryMentoring(savedCategory2, savedMentoring)
        );

        //when
        categoryMentoringRepository.delete(categoryMentoring);

        em.flush();
        em.clear();

        //then
        List<CategoryMentoring> deletedCategoryMentoring = categoryMentoringRepository.findAllDeleted();
        CategoryMentoring actual = deletedCategoryMentoring.getFirst();

        assertSoftly(softly -> {
            softly.assertThat(actual.isDeleted()).isTrue();
            softly.assertThat(actual.getDeletedAt()).isNotNull();
        });
    }
}
