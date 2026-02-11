package fittoring.integration;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import fittoring.AbstractApiDocumentationTest;
import fittoring.application.FixtureUtil;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.member.presentation.dto.request.MemberInfoUpdateRequest;
import fittoring.application.member.presentation.dto.response.MyInfoSummaryResponse;
import fittoring.application.member.repository.MemberRepository;
import fittoring.domain.model.Gender;
import fittoring.domain.model.Member;
import fittoring.domain.model.Phone;
import fittoring.domain.model.password.Password;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.assertj.core.api.SoftAssertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.JsonFieldType;

class MemberIntegrationTest extends AbstractApiDocumentationTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("로그인 중에 사용자는 자신의 정보를 조회할 수가 있다.")
    @Test
    void loginGetMyInfo() {
        // given
        Member mentee = memberRepository.save(
                new Member("id", Gender.MALE, "멘티1", new Phone("010-1231-1231"), Password.from("pw")));
        String accessToken = jwtProvider.createAccessToken(mentee.getId(), mentee.getRole());

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("member/get-members-me-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("회원")
                                .summary("자신의 정보 조회")
                                .description("로그인한 사용자 정보를 조회합니다. 성공 시 200 OK, 실패 시 401 Unauthorized를 반환합니다.")
                                .responseSchema(Schema.schema("MemberInfoResponse"))
                                .responseFields(
                                        fieldWithPath("image").type(JsonFieldType.STRING).description("이미지 URL")
                                                .optional(),
                                        fieldWithPath("loginId").type(JsonFieldType.STRING).description("로그인 ID"),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                        fieldWithPath("gender").type(JsonFieldType.STRING).description("성별"),
                                        fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("전화번호")
                                )
                                .build())))
                .cookie("accessToken", accessToken)
                .log().all()
                .when()
                .get("/members/me")
                .then()
                .statusCode(200)
                .body("loginId", Matchers.equalTo("id"))
                .body("name", Matchers.equalTo("멘티1"));
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
                .filter(documentWithTag("member/get-members-me-unauthorized",
                        resource(ResourceSnippetParameters.builder()
                                .tag("회원")
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
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

        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());

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
                .filter(documentWithTag("member/patch-memberInfo-success-partial",
                        resource(ResourceSnippetParameters.builder()
                                .tag("회원")
                                .summary("회원 정보 수정")
                                .description("회원 정보를 수정합니다. 성공 시 204 No Content, 실패 시 400 Bad Request를 반환합니다.")
                                .requestSchema(Schema.schema("MemberInfoUpdateRequest"))
                                .requestFields(
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("이름").optional(),
                                        fieldWithPath("gender").type(JsonFieldType.STRING).description("성별").optional(),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                                                .optional(),
                                        fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("전화번호")
                                                .optional()
                                )
                                .build())))
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

        String newName = "newName";
        String newPhoneNumber = "010-5678-9123";

        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());
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
                .filter(documentWithTag("member/patch-memberInfo-success-optional",
                        resource(ResourceSnippetParameters.builder()
                                .tag("회원")
                                .summary("회원 정보 수정 - 선택적 수정")
                                .description("일부 정보만 선택하여 수정할 수 있습니다. 성공 시 204 No Content를 반환합니다.")
                                .requestSchema(Schema.schema("MemberInfoUpdateRequest"))
                                .build())))
                .log().all()
                .body(request)
                .when()
                .patch("/members/me")
                .then()
                .log().all()
                .statusCode(204);
    }

    @DisplayName("회원(멘토, 멘티)은 이미 다른 사용자가 사용중인 전화번호로 변경을 할 수 없다.")
    @Test
    void updateInfo3() {
        // given
        String rawName = "이름";
        Gender rawGender = Gender.MALE;
        String rawPhoneNumber = "010-1111-2222";
        Password rawPassword = Password.from("password");
        Member member = memberRepository.save(
                new Member(
                        "menteeId1",
                        rawGender,
                        rawName,
                        new Phone(rawPhoneNumber),
                        rawPassword
                )
        );

        Member testMentee = FixtureUtil.testMentee();
        memberRepository.save(testMentee);

        String newName = "newName";
        String newPhoneNumber = testMentee.getPhoneNumber();

        MemberInfoUpdateRequest request = new MemberInfoUpdateRequest(
                newName,
                null,
                null,
                newPhoneNumber
        );

        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .filter(documentWithTag("member/patch-memberInfo-fail-duplicated-phoneNumber",
                        resource(ResourceSnippetParameters.builder()
                                .tag("회원")
                                .requestSchema(Schema.schema("MemberInfoUpdateRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all()
                .body(request)
                .when()
                .patch("/members/me")
                .then()
                .log().all()
                .statusCode(400);
    }

    @DisplayName("사용자는 수정하려는 정보가 없는 경우 회원 정보를 수정할 수 없다")
    @Test
    void emptyRequestByUpdate() {
        //given
        Member member = FixtureUtil.testMentee();
        memberRepository.save(member);

        MemberInfoUpdateRequest request = new MemberInfoUpdateRequest(
                null,
                null,
                null,
                null
        );

        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());

        //when //then
        RestAssured
                .given(spec)
                .accept("application/json")
                .contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .filter(documentWithTag("member/patch-memberInfo-fail-empty-request",
                        resource(ResourceSnippetParameters.builder()
                                .tag("회원")
                                .requestSchema(Schema.schema("MemberInfoUpdateRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all()
                .body(request)
                .when()
                .patch("/members/me")
                .then()
                .log().all()
                .statusCode(400);
    }

    @DisplayName("회원은 자신의 요약 정보를 조회할 수 있다.")
    @Test
    void getMyInfoSummary() {
        // given
        Member member = memberRepository.save(FixtureUtil.testMentee());
        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());

        // when
        MyInfoSummaryResponse actual = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("member/get-members-summary-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("회원")
                                .summary("내 요약 정보 조회")
                                .description("로그인한 회원의 요약 정보를 조회합니다. 성공 시 200 OK를 반환합니다.")
                                .responseSchema(Schema.schema("MyInfoSummaryResponse"))
                                .responseFields(
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                        fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("전화번호")
                                )
                                .build())))
                .cookie("accessToken", accessToken)
                .log().all()
                .when()
                .get("/members/summary")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .as(MyInfoSummaryResponse.class);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actual.name()).isEqualTo(member.getName());
            softly.assertThat(actual.phoneNumber()).isEqualTo(member.getPhoneNumber());
        });
    }
}
