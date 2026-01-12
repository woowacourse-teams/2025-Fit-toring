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

        @DisplayName("2페이지 조회 시 나머지 아이템이 반환된다")
        @Test
        void successFindMembersPage2() {
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
                    .filter(documentWithTag("admin/members/get-members-page-2"))
                    .queryParam("page", 2) // 0-based index, so 1 is the second page
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
                softAssertions.assertThat(actual.content()).hasSize(1);
                softAssertions.assertThat(actual.totalPages()).isEqualTo(2);
            });
        }

        @DisplayName("멤버가 없을 때 빈 페이지가 정상 반환된다")
        @Test
        void successFindMembersEmpty() {
            // given
            String adminAccessToken = jwtProvider.createAccessToken(1L, MemberRole.ADMIN);

            // when
            PageResult<AdminMemberResponse> actual = RestAssured
                    .given(spec)
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", adminAccessToken)
                    .filter(documentWithTag("admin/members/get-members-empty"))
                    .when()
                    .get("/admin/members")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<>() {
                    });

            // then
            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(actual.total()).isEqualTo(0);
                softAssertions.assertThat(actual.content()).isEmpty();
                softAssertions.assertThat(actual.totalPages()).isEqualTo(1);
            });
        }

        @DisplayName("페이지 크기 파라미터를 변경하여 조회할 수 있다")
        @Test
        void successFindMembersWithSize() {
            // given
            for (int i = 1; i <= 15; i++) {
                memberRepository.save(FixtureUtil.getTestMentee(i));
            }

            String adminAccessToken = jwtProvider.createAccessToken(1L, MemberRole.ADMIN);
            int size = 10;

            // when
            PageResult<AdminMemberResponse> actual = RestAssured
                    .given(spec)
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", adminAccessToken)
                    .filter(documentWithTag("admin/members/get-members-with-size"))
                    .queryParam("size", size)
                    .when()
                    .get("/admin/members")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<>() {
                    });

            // then
            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(actual.total()).isEqualTo(15);
                softAssertions.assertThat(actual.content()).hasSize(10);
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

        @DisplayName("토큰 없이 요청 시 401 Unauthorized를 반환한다")
        @Test
        void failFindMembersWithoutToken() {
            // when // then
            RestAssured
                    .given(spec)
                    .log().all().contentType(ContentType.JSON)
                    .filter(documentWithTag("admin/members/get-members-unauthorized"))
                    .when()
                    .get("/admin/members")
                    .then().log().all()
                    .statusCode(401);
        }
    }
}
