package fittoring.admin.service;

import fittoring.admin.presentation.dto.AdminMemberResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.admin.repository.CustomMemberRepositoryImpl;
import fittoring.application.FixtureUtil;
import fittoring.application.mentoring.repository.MentoringPaginationHelper;
import fittoring.config.QueryDslConfig;
import fittoring.domain.model.Member;
import fittoring.util.DbCleaner;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({
        AdminMemberService.class,
        CustomMemberRepositoryImpl.class,
        DbCleaner.class,
        QueryDslConfig.class
})
@DataJpaTest
class AdminMemberServiceTest {

    @Autowired
    private AdminMemberService adminMemberService;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private DbCleaner dbCleaner;

    @MockitoBean
    private MentoringPaginationHelper mph;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
    }

    @DisplayName("관리자 멤버 페이징 조회")
    @Nested
    class adminMemberPaging {

        @DisplayName("관리자는 전체 멤버를 페이징하여 조회할 수 있다. - 첫 페이지 조회")
        @Test
        void findAllForAdminPaged() {
            // given
            Member admin = em.persist(FixtureUtil.getTestAdmin());
            for (int i = 1; i <= 12; i++) {
                em.persist(FixtureUtil.getTestMentee(i));
            }

            // when
            PageResult<AdminMemberResponse> pageResult = adminMemberService.findAllForAdminPaged(admin.getId(), 1, 5);

            // then
            List<AdminMemberResponse> result = pageResult.content();
            List<String> loginIds = result.stream().map(AdminMemberResponse::loginId).toList();

            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(loginIds).containsExactly(
                        "menteeId12",
                        "menteeId11",
                        "menteeId10",
                        "menteeId9",
                        "menteeId8");
            });
        }

        @DisplayName("관리자는 전체 멤버를 페이징하여 조회할 수 있다. - 중간 페이지 조회")
        @Test
        void findAllForAdminPaged2() {
            // given
            Member admin = em.persist(FixtureUtil.getTestAdmin());
            for (int i = 1; i <= 12; i++) {
                em.persist(FixtureUtil.getTestMentee(i));
            }

            // when
            PageResult<AdminMemberResponse> pageResult = adminMemberService.findAllForAdminPaged(admin.getId(), 2, 5);

            // then
            List<AdminMemberResponse> result = pageResult.content();
            List<String> loginIds = result.stream().map(AdminMemberResponse::loginId).toList();

            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(loginIds).containsExactly(
                        "menteeId7",
                        "menteeId6",
                        "menteeId5",
                        "menteeId4",
                        "menteeId3");
            });
        }

        @DisplayName("관리자는 전체 멤버를 페이징하여 조회할 수 있다. - 마지막 페이지 조회")
        @Test
        void findAllForAdminPaged3() {
            // given
            Member admin = em.persist(FixtureUtil.getTestAdmin());
            for (int i = 1; i <= 12; i++) {
                em.persist(FixtureUtil.getTestMentee(i));
            }

            // when
            PageResult<AdminMemberResponse> pageResult = adminMemberService.findAllForAdminPaged(admin.getId(), 3, 5);

            // then
            List<AdminMemberResponse> result = pageResult.content();
            List<String> loginIds = result.stream().map(AdminMemberResponse::loginId).toList();

            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(loginIds).containsExactly(
                        "menteeId2",
                        "menteeId1",
                        "adminId");
            });
        }
    }
}
