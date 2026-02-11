package fittoring.integration.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import fittoring.AbstractApiDocumentationTest;
import fittoring.admin.presentation.dto.AdminMentoringResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.application.FixtureUtil;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.CategoryMentoringRepository;
import fittoring.application.mentoring.repository.CategoryRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.domain.model.Category;
import fittoring.domain.model.CategoryMentoring;
import fittoring.domain.model.Gender;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberRole;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Phone;
import fittoring.domain.model.password.Password;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AdminMentoringIntegrationTest extends AbstractApiDocumentationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMentoringRepository categoryMentoringRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @DisplayName("관리자는 멘토링 목록을 페이징하여 조회할 수 있다.")
    @Test
    void getMentorings() {
        // given
        List<Member> mentors = new ArrayList<>();
        for (int i = 1; i <= 21; i++) {
            Member testMentor = FixtureUtil.testMentor(i);
            mentors.add(testMentor);
        }
        memberRepository.saveAll(mentors);

        List<Mentoring> mentorings = new ArrayList<>();
        for (int i = 1; i <= 21; i++) {
            Mentoring testMentoring = FixtureUtil.testMentoring(mentors.get(i - 1));
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

        Member testAdmin = memberRepository.save(FixtureUtil.testAdmin());

        String accessToken = jwtProvider.createAccessToken(testAdmin.getId(), testAdmin.getRole());

        // when
        PageResult<AdminMentoringResponse> response = RestAssured
                .given(spec)
                .log().all()
                .contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .filter(documentWithTag("admin/mentorings/get-mentorings-success"))
                .queryParam("page", 1, "size", 20)
                .when()
                .get("/admin/mentorings")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertSoftly(softly -> {
            softly.assertThat(response.page()).isEqualTo(0);
            softly.assertThat(response.size()).isEqualTo(20);
            softly.assertThat(response.content()).hasSize(20);
            softly.assertThat(response.total()).isEqualTo(21);
            softly.assertThat(response.totalPages()).isEqualTo(2);
        });
    }

    @DisplayName("관리자가 특정 멘토링을 삭제하면 204 No Content를 반환한다.")
    @Test
    void deleteMentoring() {
        // given
        Member admin = memberRepository.save(new Member(
                "admin",
                Gender.MALE,
                "관리자",
                new Phone("010-1234-5678"),
                Password.from("pw"),
                MemberRole.ADMIN
        ));
        String accessToken = jwtProvider.createAccessToken(admin.getId(), admin.getRole());

        Member mentor = memberRepository.save(new Member(
                "mentor",
                Gender.MALE,
                "멘토",
                new Phone("010-9876-5432"),
                Password.from("pw")
        ));

        Mentoring mentoring = mentoringRepository.save(new Mentoring(
                mentor,
                50000,
                5,
                "삭제될 멘토링",
                "삭제될 멘토링 상세"
        ));

        // when
        RestAssured
                .given(spec)
                .log().all()
                .contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .filter(documentWithTag("admin/mentorings/delete-mentoring-success"))
                .when()
                .delete("/admin/mentorings/{mentoringId}", mentoring.getId())
                .then().log().all()
                .statusCode(204);

        // then
        assertThat(mentoringRepository.findById(mentoring.getId())).isEmpty();
    }

    @DisplayName("관리자가 아닌 일반 사용자가 멘토링 목록 조회를 하면 403 Forbidden을 반환한다.")
    @Test
    void getMentorings_Forbidden() {
        // given
        Member user = memberRepository.save(new Member(
                "user",
                Gender.MALE,
                "일반유저",
                new Phone("010-1111-2222"),
                Password.from("pw"),
                MemberRole.MENTEE
        ));
        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getRole());

        // when & then
        RestAssured
                .given(spec)
                .log().all()
                .contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .filter(documentWithTag("admin/mentorings/get-mentorings-forbidden"))
                .when()
                .get("/admin/mentorings")
                .then().log().all()
                .statusCode(403);
    }

    @DisplayName("관리자가 아닌 일반 사용자가 멘토링 삭제를 하면 403 Forbidden을 반환한다.")
    @Test
    void deleteMentoring_Forbidden() {
        // given
        Member user = memberRepository.save(new Member(
                "user",
                Gender.MALE,
                "일반유저",
                new Phone("010-1111-2222"),
                Password.from("pw"),
                MemberRole.MENTEE
        ));
        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getRole());

        // when & then
        RestAssured
                .given(spec)
                .log().all()
                .contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .filter(documentWithTag("admin/mentorings/delete-mentoring-forbidden"))
                .when()
                .delete("/admin/mentorings/{mentoringId}", 1)
                .then().log().all()
                .statusCode(403);
    }
}
