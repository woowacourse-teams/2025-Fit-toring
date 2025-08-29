package fittoring.integration.mentoring.api;

import com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.Phone;
import fittoring.mentoring.business.model.password.Password;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.business.service.JwtProvider;
import fittoring.util.DbCleaner;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.restassured.RestAssuredRestDocumentationConfigurer;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import org.springframework.test.context.ActiveProfiles;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.ResourceSnippetParameters.builder;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

@ActiveProfiles("test")
@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MemberControllerTest {

    private RequestSpecification spec;

    private RestDocumentationFilter documentWithTag(String id) {
        String tag = id.contains("/") ? id.substring(0, id.indexOf('/')) : id;
        return RestAssuredRestDocumentationWrapper.document(id, resource(builder().tag(tag).build()));
    }

    @LocalServerPort
    private int port;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DbCleaner dbCleaner;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        dbCleaner.clean();
        RestAssuredRestDocumentationConfigurer restAssuredConfig = documentationConfiguration(restDocumentation);
        restAssuredConfig.operationPreprocessors()
                .withRequestDefaults(prettyPrint())
                .withResponseDefaults(prettyPrint());
        spec = new RequestSpecBuilder()
                .addFilter(restAssuredConfig)
                .build();
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
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("member/get-members-me-success"))
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
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("member/get-members-me-unauthorized"))
                .cookie("accessToken", null)
                .when()
                .get("/members/me")
                .then()
                .statusCode(401);
    }
}
