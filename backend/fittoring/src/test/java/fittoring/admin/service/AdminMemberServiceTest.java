package fittoring.admin.service;

import fittoring.IntegrationTestSupport;
import fittoring.admin.presentation.dto.AdminMemberResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.application.FixtureUtil;
import fittoring.application.member.repository.MemberRepository;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AdminMemberServiceTest extends IntegrationTestSupport {

    @Autowired
    private AdminMemberService adminMemberService;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("관리자 멤버 페이징 조회")
    @Nested
    class adminMemberPaging {

        @DisplayName("전체 멤버를 페이징하여 조회할 수 있다. - 첫 페이지 조회")
        @Test
        void findAllForAdminPaged() {
            // given
            for (int i = 1; i <= 12; i++) {
                memberRepository.save(FixtureUtil.getTestMentee(i));
            }

            // when
            PageResult<AdminMemberResponse> pageResult = adminMemberService.findAllForAdminPaged(1, 5);

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
                softAssertions.assertThat(pageResult.total()).isEqualTo(12);
                softAssertions.assertThat(pageResult.totalPages()).isEqualTo(3);
            });
        }

        @DisplayName("전체 멤버를 페이징하여 조회할 수 있다. - 중간 페이지 조회")
        @Test
        void findAllForAdminPaged2() {
            // given
            for (int i = 1; i <= 12; i++) {
                memberRepository.save(FixtureUtil.getTestMentee(i));
            }

            // when
            PageResult<AdminMemberResponse> pageResult = adminMemberService.findAllForAdminPaged(2, 5);

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
                softAssertions.assertThat(pageResult.total()).isEqualTo(12);
                softAssertions.assertThat(pageResult.totalPages()).isEqualTo(3);
            });
        }

        @DisplayName("전체 멤버를 페이징하여 조회할 수 있다. - 마지막 페이지 조회")
        @Test
        void findAllForAdminPaged3() {
            // given
            for (int i = 1; i <= 12; i++) {
                memberRepository.save(FixtureUtil.getTestMentee(i));
            }

            // when
            PageResult<AdminMemberResponse> pageResult = adminMemberService.findAllForAdminPaged(3, 5);

            // then
            List<AdminMemberResponse> result = pageResult.content();
            List<String> loginIds = result.stream().map(AdminMemberResponse::loginId).toList();

            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(loginIds).containsExactly(
                        "menteeId2",
                        "menteeId1");
                softAssertions.assertThat(pageResult.total()).isEqualTo(12);
                softAssertions.assertThat(pageResult.totalPages()).isEqualTo(3);
            });
        }
    }
}
