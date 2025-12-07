package fittoring.integration;

import fittoring.AbstractApiDocumentationTest;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.member.presentation.dto.request.MemberInfoUpdateRequest;
import fittoring.application.member.repository.MemberRepository;
import fittoring.domain.model.Gender;
import fittoring.domain.model.Member;
import fittoring.domain.model.Phone;
import fittoring.domain.model.password.Password;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberIntegrationTest extends AbstractApiDocumentationTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("로그인 중에 멘티는 내 정보를 조회할 수가 있다.")
    @Test
    void loginGetMyInfoForMentee() {
        // given
        Member mentee = memberRepository.save(
                new Member("id", Gender.MALE, "멘티1", new Phone("010-1231-1231"), Password.from("pw")));
        String accessToken = jwtProvider.createAccessToken(mentee.getId());

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("member/get-members-me-success"))
                .cookie("accessToken", accessToken)
                .log().all()
                .when()
                .get("/members/me")
                .then()
                .statusCode(200)
                .body("loginId", Matchers.equalTo("id"))
                .body("name", Matchers.equalTo("멘티1"));
    }

    @DisplayName("로그인 중에 멘토는 내 정보를 조회할 수 있다.")
    @Test
    void loginGetMyInfoForMentor() {
        // given
        Member mentor = memberRepository.save(
                new Member("id", Gender.MALE, "멘토1", new Phone("010-1231-1231"), Password.from("pw")));
        mentor.registerAsMentor();
        String accessToken = jwtProvider.createAccessToken(mentor.getId());

        // when
        // then
        RestAssured
                .given()
                .cookie("accessToken", accessToken)
                .log().all().then()
                .when()
                .get("/members/me")
                .then()
                .statusCode(200)
                .body("loginId", Matchers.equalTo("id"))
                .body("name", Matchers.equalTo("멘토1"));
    }

    @DisplayName("비로그인 중에 멘티는 내 정보를 조회할 수 없다.")
    @Test
    void nonLoginGetMyInfo() {
        // given
        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("member/get-members-me-unauthorized"))
                .cookie("accessToken", null)
                .when()
                .get("/members/me")
                .then()
                .statusCode(401);
    }

    @DisplayName("회원(멘토, 멘티)은 자신의 회원 정보인 이름, 성별, 비밀번호, 전화번호를 수정할 수 있다. 수정에 성공하면 204 상태코드를 응답한다.")
    @Test
    void updateInfo() {
        // given
        String rawName = "이름";
        Gender rawGender = Gender.MALE;
        String rawPhoneNumber = "010-1234-5678";
        Password rawPassword = Password.from("password");
        Member member = memberRepository.save(
                new Member(
                        "menteeId",
                        rawGender,
                        rawName,
                        new Phone(rawPhoneNumber),
                        rawPassword
                )
        );

        String accessToken = jwtProvider.createAccessToken(member.getId());

        String newName = "newName";
        Gender newGender = Gender.FEMALE;
        String newPassword = "newPassword";
        String newPhoneNumber = "010-5678-9123";

        MemberInfoUpdateRequest request = new MemberInfoUpdateRequest(
                newName,
                newGender,
                newPassword,
                newPhoneNumber
        );

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .filter(documentWithTag("member/patch-memberInfo-success-partial"))
                .log().all()
                .body(request)
                .when()
                .patch("/members/me")
                .then()
                .log().all()
                .statusCode(204);
    }

    @DisplayName("회원(멘토, 멘티)은 자신의 이름, 성별, 비밀번호, 전화번호 중 일부를 선택적으로 수정할 수 있다.")
    @Test
    void updateInfo2() {
        // given
        String rawName = "이름";
        Gender rawGender = Gender.MALE;
        String rawPhoneNumber = "010-1234-5678";
        Password rawPassword = Password.from("password");
        Member member = memberRepository.save(
                new Member(
                        "menteeId",
                        rawGender,
                        rawName,
                        new Phone(rawPhoneNumber),
                        rawPassword
                )
        );

        String accessToken = jwtProvider.createAccessToken(member.getId());

        String newName = "newName";
        String newPhoneNumber = "010-5678-9123";

        MemberInfoUpdateRequest request = new MemberInfoUpdateRequest(
                newName,
                null,
                null,
                newPhoneNumber
        );

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .filter(documentWithTag("member/patch-memberInfo-success-optional"))
                .log().all()
                .body(request)
                .when()
                .patch("/members/me")
                .then()
                .log().all()
                .statusCode(204);
    }
}
