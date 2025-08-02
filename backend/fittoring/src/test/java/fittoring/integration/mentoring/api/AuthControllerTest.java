package fittoring.integration.mentoring.api;

import static org.assertj.core.api.Assertions.assertThat;

import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.presentation.dto.SignUpRequest;
import fittoring.mentoring.presentation.dto.ValidateDuplicateLoginIdRequest;
import fittoring.mentoring.presentation.dto.VerifyPhoneNumberRequest;
import fittoring.util.DbCleaner;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    @LocalServerPort
    public int port;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        dbCleaner.clean();
    }

    @DisplayName("사용자는 회원가입을 할 수 있다.")
    @Test
    void signUp() {
        //given
        String loginId = "loginId";
        String name = "이름";
        String gender = "남";
        String phone = "010-1234-5678";
        String password = "password";
        SignUpRequest request = new SignUpRequest(loginId, name, gender, phone, password);

        //when
        RestAssured
                .given()
                .log().all().contentType(ContentType.JSON)
                .when()
                .body(request)
                .post("/signup")
                .then().log().all()
                .statusCode(201)
                .extract();

        //then
        assertThat(memberRepository.findById(1L)).isNotNull();
    }

    @DisplayName("사용자는 유효하지 않은 정보로 회원가입을 할 수 없다.")
    @Test
    void signUp2() {
        //given
        String loginId = null;
        String name = "이름";
        String gender = "남";
        String phone = "010-1234-5678";
        String password = "password";
        SignUpRequest request = new SignUpRequest(loginId, name, gender, phone, password);

        //when
        Response response = RestAssured
                .given()
                .log().all().contentType(ContentType.JSON)
                .when()
                .body(request)
                .post("/signup");

        //then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @DisplayName("사용자는 중복된 아이디로 회원가입을 할 수 없다.")
    @Test
    void signUp3() {
        //given
        Member member = new Member(
                "loginId",
                "이름",
                "남",
                "010-1234-5678",
                Password.from("password")
        );

        memberRepository.save(member);

        String loginId = "loginId";
        String name = "이름";
        String gender = "남";
        String phone = "010-1234-5678";
        String password = "password";
        SignUpRequest request = new SignUpRequest(loginId, name, gender, phone, password);

        //when
        Response response = RestAssured
                .given()
                .log().all().contentType(ContentType.JSON)
                .when()
                .body(request)
                .post("/signup");

        //then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @DisplayName("사용자는 중복되지 않은 아이디로 아이디 중복 검증을 시도할 경우 200 상태코드를 받는다.")
    @Test
    void validateDuplicateLoginId() {
        //given
        ValidateDuplicateLoginIdRequest request = new ValidateDuplicateLoginIdRequest("uniqueLoginId");

        //when
        Response response = RestAssured
                .given()
                .log().all().contentType(ContentType.JSON)
                .when()
                .body(request)
                .post("/validate-id");

        //then
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @DisplayName("사용자는 중복된 아이디로 회원가입을 시도할 경우 400 상태코드를 받는다.")
    @Test
    void validateDuplicateLoginId2() {
        //given
        memberRepository.save(
                new Member(
                        "uniqueLoginId",
                        "이름",
                        "남",
                        "010-1234-5678",
                        Password.from("password")
                )
        );

        memberRepository.save(
                new Member(
                        "LoginId",
                        "이름",
                        "남",
                        "010-5678-9123",
                        Password.from("password")
                )
        );

        ValidateDuplicateLoginIdRequest request = new ValidateDuplicateLoginIdRequest("uniqueLoginId");

        //when
        Response response = RestAssured
                .given()
                .log().all().contentType(ContentType.JSON)
                .when()
                .body(request)
                .post("/validate-id");

        //then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @DisplayName("사용자는 잘못된 전화번호 형식으로 인증을 요청하면 400 상태코드를 받는다.")
    @Test
    void invalidPhoneNumberVerification() {
        // given
        String nonHyphenPhone = "01012345678";
        VerifyPhoneNumberRequest request = new VerifyPhoneNumberRequest(nonHyphenPhone);

        // when
        // then
        RestAssured.given()
                .log().all().contentType(ContentType.JSON)
                .when()
                .body(request)
                .when()
                .post("/auth-code")
                .then()
                .log().all()
                .statusCode(400);
    }
}
