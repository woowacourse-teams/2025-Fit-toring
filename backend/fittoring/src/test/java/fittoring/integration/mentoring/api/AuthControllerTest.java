package fittoring.integration.mentoring.api;

import static org.assertj.core.api.Assertions.assertThat;

import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.presentation.dto.SignUpRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("사용자는 회원가입을 할 수 있다.")
    @Test
    void signUp() {
        //given
        String loginId = "loginId";
        String name = "이름";
        String male = "남";
        String phone = "010-1234-5678";
        String password = "password";
        SignUpRequest request = new SignUpRequest(loginId, name, male, phone, password);

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

}
