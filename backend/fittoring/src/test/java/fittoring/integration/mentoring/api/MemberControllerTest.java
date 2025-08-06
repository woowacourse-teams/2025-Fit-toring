package fittoring.integration.mentoring.api;

import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.business.service.JwtProvider;
import fittoring.util.DbCleaner;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MemberControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        dbCleaner.clean();
    }

    @DisplayName("로그인 중에 멘티는 내 정보를 조회할 수 있다.")
    @Test
    void loginGetMyInfoForMentee() {
        // given
        Member mentee = memberRepository.save(
                new Member("id", "MALE", "멘티1", new Phone("010-1231-1231"), Password.from("pw")));
        String accessToken = jwtProvider.createAccessToken(mentee.getId());

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
                .body("name", Matchers.equalTo("멘티1"));
    }

    @DisplayName("로그인 중에 멘토는 내 정보를 조회할 수 있다.")
    @Test
    void loginGetMyInfoForMentor() {
        // given
        Member mentor = memberRepository.save(
                new Member("id", "MALE", "멘토1", new Phone("010-1231-1231"), Password.from("pw")));
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
                .given()
                .cookie("accessToken", null)
                .when()
                .get("/members/me")
                .then()
                .statusCode(401);
    }
}
