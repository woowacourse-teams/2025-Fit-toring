package fittoring.admin.service;

import fittoring.admin.presentation.dto.AdminMentoringResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.application.FixtureUtil;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.CategoryMentoringRepository;
import fittoring.application.mentoring.repository.CategoryRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.domain.model.Category;
import fittoring.domain.model.CategoryMentoring;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import fittoring.util.DbCleaner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class AdminMentoringServiceTest {

    @Autowired
    private AdminMentoringService adminMentoringService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMentoringRepository categoryMentoringRepository;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }

    @DisplayName("관리자는 멘토링 목록을 조회할 수 있다. 조회시 페이지네이션으로 조회하게 된다. 한 페이지당 10개로 제한한다.")
    @Test
    void getMentorings() {
        //given
        List<Member> mentors = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            Member testMentor = FixtureUtil.getTestMentor(i);
            mentors.add(testMentor);
        }
        memberRepository.saveAll(mentors);

        List<Mentoring> mentorings = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            Mentoring testMentoring = FixtureUtil.getTestMentoring(mentors.get(i - 1));
            mentorings.add(testMentoring);
        }
        mentoringRepository.saveAll(mentorings);

        List<Category> categories = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Category testCategory = new Category("카테고리" + i);
            categories.add(testCategory);
        }
        categoryRepository.saveAll(categories);

        List<CategoryMentoring> categoryMentorings = new ArrayList<>();
        Random random = new Random();
        for (Mentoring mentoring : mentorings) {
            List<Integer> categoryIndexes = IntStream.range(0, categories.size()).boxed().collect(Collectors.toList());
            Collections.shuffle(categoryIndexes);

            int categoriesCount = random.nextInt(4) + 1;
            for (int i = 0; i < categoriesCount; i++) {
                Category category = categories.get(categoryIndexes.get(i));
                categoryMentorings.add(new CategoryMentoring(category, mentoring));
            }
        }
        categoryMentoringRepository.saveAll(categoryMentorings);

        Member testAdmin = memberRepository.save(FixtureUtil.getTestAdmin());

        //when
        PageResult<AdminMentoringResponse> allForAdminPaged = adminMentoringService.findAllForAdminPaged(
                testAdmin.getId(), 1);
        PageResult<AdminMentoringResponse> allForAdminPaged1 = adminMentoringService.findAllForAdminPaged(
                testAdmin.getId(), 2);
        PageResult<AdminMentoringResponse> allForAdminPaged2 = adminMentoringService.findAllForAdminPaged(
                testAdmin.getId(), 3);

        //then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(allForAdminPaged.content()).hasSize(10);
            softly.assertThat(allForAdminPaged1.content()).hasSize(10);
            softly.assertThat(allForAdminPaged2.content()).hasSize(10);

            softly.assertThat(allForAdminPaged.page()).isEqualTo(0);
            softly.assertThat(allForAdminPaged1.page()).isEqualTo(1);
            softly.assertThat(allForAdminPaged2.page()).isEqualTo(2);

            softly.assertThat(allForAdminPaged.total()).isEqualTo(30);
            softly.assertThat(allForAdminPaged1.total()).isEqualTo(30);
            softly.assertThat(allForAdminPaged2.total()).isEqualTo(30);

            softly.assertThat(allForAdminPaged.totalPages()).isEqualTo(3);
            softly.assertThat(allForAdminPaged1.totalPages()).isEqualTo(3);
            softly.assertThat(allForAdminPaged2.totalPages()).isEqualTo(3);

            softly.assertThat(allForAdminPaged.content())
                    .extracting(AdminMentoringResponse::mentoringId)
                    .isSortedAccordingTo(Comparator.reverseOrder());
            softly.assertThat(allForAdminPaged1.content())
                    .extracting(AdminMentoringResponse::mentoringId)
                    .isSortedAccordingTo(Comparator.reverseOrder());
            softly.assertThat(allForAdminPaged2.content())
                    .extracting(AdminMentoringResponse::mentoringId)
                    .isSortedAccordingTo(Comparator.reverseOrder());
        });
    }
}
