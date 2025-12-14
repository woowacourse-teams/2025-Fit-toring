package fittoring.integration.admin;

import fittoring.AbstractApiDocumentationTest;
import fittoring.admin.presentation.dto.AdminMemberResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.member.repository.MemberRepository;
import fittoring.domain.model.Gender;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberRole;
import fittoring.domain.model.Phone;
import fittoring.domain.model.password.Password;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AdminMemberIntegrationTest extends AbstractApiDocumentationTest {

    private Member admin;
    private Member user;
    private String adminAccessToken;
    private String userAccessToken;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        admin = memberRepository.save(new Member(
                "adminId",
                Gender.FEMALE,
                "관리자",
                new Phone("010-0000-0000"),
                Password.from("pw"),
                MemberRole.ADMIN
        ));
        adminAccessToken = jwtProvider.createAccessToken(admin.getId());
        user = memberRepository.save(new Member(
                "userId",
                Gender.MALE,
                "멘티",
                new Phone("010-1111-1111"),
                Password.from("pw")
        ));
        userAccessToken = jwtProvider.createAccessToken(user.getId());
    }

    @DisplayName("관리자 사용자 목록 조회")
    @Nested
    class MembersForAdmin {

        @DisplayName("관리자 권한이 없다면 멤버 목록 조회에 실패한다")
        @Test
        void failFindMembersForAdminWithoutAdmin() {
            // given
            // when
            // then
            RestAssured
                    .given()
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", userAccessToken)
                    .when()
                    .get("/admin/members")
                    .then().log().all()
                    .statusCode(403);
        }

        @DisplayName("관리자 권한이 있다면 멤버 목록 조회에 성공한다")
        @Test
        void successFindMembersForAdminWithAdmin() {
            // given
            // when
            var actual = RestAssured
                    .given()
                    .log().all().contentType(ContentType.JSON)
                    .cookie("accessToken", adminAccessToken)
                    .when()
                    .get("/admin/members")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<PageResult<AdminMemberResponse>>() {
                    });
            List<AdminMemberResponse> content = actual.content();
            // then
            var expected = List.of(
                    new AdminMemberResponse(
                            admin.getName(),
                            admin.getLoginId(),
                            admin.getGender(),
                            admin.getPhoneNumber(),
                            admin.getRole()
                    ),
                    new AdminMemberResponse(
                            user.getName(),
                            user.getLoginId(),
                            user.getGender(),
                            user.getPhoneNumber(),
                            user.getRole()
                    )
            );
            Assertions.assertThat(content)
                    .containsExactlyInAnyOrderElementsOf(expected);
        }
    }
}
