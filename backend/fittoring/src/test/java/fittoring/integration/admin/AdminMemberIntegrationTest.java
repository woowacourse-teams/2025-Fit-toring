package fittoring.integration.admin;

import fittoring.AbstractApiDocumentationTest;
import fittoring.admin.presentation.dto.AdminMemberResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.application.FixtureUtil;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.member.repository.MemberRepository;
import fittoring.domain.model.MemberRole;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AdminMemberIntegrationTest extends AbstractApiDocumentationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @DisplayName("관리자 사용자 목록 페이징 조회")
    @Nested
    class MembersForAdmin {
        @DisplayName("관리자 권한이 있다면 멤버 목록 조회에 성공한다")
        @Test
        void successFindMembersForAdminWithAdmin() {
            // given
            for (int i = 1; i <= 21; i++) {
                memberRepository.save(FixtureUtil.getTestMentee(i));
            }

            String adminAccessToken = jwtProvider.createAccessToken(1L, MemberRole.ADMIN);

            // when
            PageResult<AdminMemberResponse> actual = RestAssured
                    .given(spec)
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", adminAccessToken)
                    .filter(documentWithTag("admin/members/get-members-success"))
                    .when()
                    .get("/admin/members")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<>() {
                    });

            // then
            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(actual.total()).isEqualTo(21);
                softAssertions.assertThat(actual.content()).hasSize(20);
                softAssertions.assertThat(actual.totalPages()).isEqualTo(2);
            });
        }

        @DisplayName("관리자 권한이 없다면 멤버 목록 조회에 실패한다")
        @Test
        void failFindMembersForAdminWithoutAdmin() {
            // given
            String userAccessToken = jwtProvider.createAccessToken(1L, MemberRole.MENTEE);

            // when // then
            RestAssured
                    .given(spec)
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", userAccessToken)
                    .filter(documentWithTag("admin/members/get-members-forbidden"))
                    .when()
                    .get("/admin/members")
                    .then().log().all()
                    .statusCode(403);
        }
    }
}
