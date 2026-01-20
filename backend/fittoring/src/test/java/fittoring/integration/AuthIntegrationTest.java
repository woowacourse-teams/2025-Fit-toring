package fittoring.integration;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import fittoring.AbstractApiDocumentationTest;
import fittoring.application.FixtureUtil;
import fittoring.application.auth.presentation.dto.request.FindLoginIdRequest;
import fittoring.application.auth.presentation.dto.request.OauthSignUpRequest;
import fittoring.application.auth.presentation.dto.request.ResetPasswordRequest;
import fittoring.application.auth.presentation.dto.request.SignInRequest;
import fittoring.application.auth.presentation.dto.request.SignUpRequest;
import fittoring.application.auth.presentation.dto.request.ValidateDuplicateLoginIdRequest;
import fittoring.application.auth.presentation.dto.request.VerificationCodeRequest;
import fittoring.application.auth.presentation.dto.request.VerifyPhoneNumberRequest;
import fittoring.application.auth.presentation.dto.response.KakaoTokenResponse;
import fittoring.application.auth.presentation.dto.response.KakaoUserInfoResponse;
import fittoring.application.auth.presentation.dto.response.LoginIdResponse;
import fittoring.application.auth.presentation.dto.response.LoginStatusDto;
import fittoring.application.auth.repository.MemberOauthRepository;
import fittoring.application.auth.repository.PhoneVerificationRepository;
import fittoring.application.auth.repository.RefreshTokenRepository;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.member.repository.MemberRepository;
import fittoring.domain.model.AuthProvider;
import fittoring.domain.model.Gender;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberOauth;
import fittoring.domain.model.MemberRole;
import fittoring.domain.model.Phone;
import fittoring.domain.model.PhoneVerification;
import fittoring.domain.model.RefreshToken;
import fittoring.domain.model.password.Password;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.JsonFieldType;

class AuthIntegrationTest extends AbstractApiDocumentationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PhoneVerificationRepository phoneVerificationRepository;

    @Autowired
    private MemberOauthRepository memberOauthRepository;

    @DisplayName("전화번호를 인증한 사용자는 회원가입을 할 수 있다.")
    @Test
    void signUp() {
        //given
        String loginId = "loginId";
        String name = "이름";
        Gender gender = Gender.MALE;
        String phoneNumber = "010-1234-5678";
        String password = "password";
        SignUpRequest request = new SignUpRequest(loginId, name, gender, phoneNumber, password);

        phoneVerificationRepository.save(
                FixtureUtil.getVerifiedPhoneVerification(new Phone(phoneNumber))
        );

        //when
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/post-signup-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .summary("회원가입")
                                .description("회원가입을 진행합니다. 성공 시 201 Created, 실패 시 400 Bad Request를 반환합니다.")
                                .requestSchema(Schema.schema("SignUpRequest"))
                                .requestFields(
                                        fieldWithPath("loginId").type(JsonFieldType.STRING).description("아이디 (5~15자)"),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("이름 (2~5자)"),
                                        fieldWithPath("gender").type(JsonFieldType.STRING)
                                                .description("성별 (MALE, FEMALE)"),
                                        fieldWithPath("phoneNumber").type(JsonFieldType.STRING)
                                                .description("전화번호 (010-XXXX-XXXX)"),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호 (5~20자)")
                                )
                                .build())))
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

    @DisplayName("전화번호 인증 정보가 없는 사용자는 회원가입을 할 수 없다.")
    @Test
    void signUp2() {
        //given
        String loginId = "loginId";
        String name = "이름";
        Gender gender = Gender.MALE;
        String phoneNumber = "010-1234-5678";
        String password = "password";
        SignUpRequest request = new SignUpRequest(loginId, name, gender, phoneNumber, password);

        //when
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/post-signup-invalid-phoneNumber-verification",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .requestSchema(Schema.schema("SignUpRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .when()
                .body(request)
                .post("/signup")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo(BusinessErrorMessage.PHONE_VERIFICATION_INVALID.getMessage()))
        ;
    }

    @DisplayName("사용자는 유효하지 않은 정보로 회원가입을 할 수 없다.")
    @Test
    void signUp3() {
        //given
        String loginId = null;
        String name = "이름";
        Gender gender = Gender.MALE;
        String phoneNumber = "010-1234-5678";
        String password = "password";
        SignUpRequest request = new SignUpRequest(loginId, name, gender, phoneNumber, password);

        //when
        Response response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/post-signup-invalid-info",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .requestSchema(Schema.schema("SignUpRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .when()
                .body(request)
                .post("/signup");

        //then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @DisplayName("사용자는 유효하지 않은 아이디로 로그인을 할 수 없고, 쿠키에 토큰이 저장되지 않는다.")
    @Test
    void login() {
        //given
        Member member = new Member(
                "loginId",
                Gender.MALE,
                "이름",
                new Phone("010-1234-5678"),
                Password.from("password")
        );
        memberRepository.save(member);

        SignInRequest request = new SignInRequest("invalidLoginId", "password");

        //when
        Response response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/post-login-invalid-loginId",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .requestSchema(Schema.schema("SignInRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .when()
                .body(request)
                .post("/login");
        //then
        List<String> cookies = response.getHeaders().getValues("Set-Cookie");

        SoftAssertions.assertSoftly(softly -> {
                    assertThat(response.statusCode()).isEqualTo(404);
                    assertThat(response.getHeaders().hasHeaderWithName("Set-Cookie")).isFalse();
                    assertThat(cookies).noneMatch(cookie -> cookie.startsWith("accessToken="));
                    assertThat(cookies).noneMatch(cookie -> cookie.startsWith("refreshToken="));
                }
        );
    }

    @DisplayName("사용자는 유효하지 않은 비밀번호로 로그인을 할 수 없고, 쿠키에 토큰이 저장되지 않는다.")
    @Test
    void login2() {
        //given
        Member member = new Member(
                "loginId",
                Gender.MALE,
                "이름",
                new Phone("010-1234-5678"),
                Password.from("password")
        );
        memberRepository.save(member);

        SignInRequest request = new SignInRequest("loginId", "invalidPassword");

        //when
        Response response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/post-login-invalid-password",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .requestSchema(Schema.schema("SignInRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .when()
                .body(request)
                .post("/login");

        //then
        List<String> cookies = response.getHeaders().getValues("Set-Cookie");

        SoftAssertions.assertSoftly(softly -> {
                    assertThat(response.statusCode()).isEqualTo(400);
                    assertThat(response.getHeaders().hasHeaderWithName("Set-Cookie")).isFalse();
                    assertThat(cookies).noneMatch(cookie -> cookie.startsWith("accessToken="));
                    assertThat(cookies).noneMatch(cookie -> cookie.startsWith("refreshToken="));
                }
        );
    }

    @DisplayName("사용자가 로그인에 성공하면 상태 코드 200을 응답하고, accessToken과 refreshToken을 쿠키에 저장한다.")
    @Test
    void login3() {
        //given
        Member member = new Member(
                "loginId",
                Gender.MALE,
                "이름",
                new Phone("010-1234-5678"),
                Password.from("password")
        );
        memberRepository.save(member);

        SignInRequest request = new SignInRequest("loginId", "password");

        //when
        Response response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/post-login-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .summary("로그인")
                                .description(
                                        "로그인을 진행하고 AccessToken과 RefreshToken을 쿠키에 발급합니다. 성공 시 200 OK, 실패 시 400 Bad Request 또는 404 Not Found를 반환합니다.")
                                .requestSchema(Schema.schema("SignInRequest"))
                                .requestFields(
                                        fieldWithPath("loginId").type(JsonFieldType.STRING).description("아이디"),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                                )
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .when()
                .body(request)
                .post("/login");

        // then
        List<String> cookies = response.getHeaders().getValues("Set-Cookie");

        SoftAssertions.assertSoftly(softly -> {
                    assertThat(response.statusCode()).isEqualTo(200);
                    assertThat(cookies).anyMatch(cookie -> cookie.startsWith("accessToken="));
                    assertThat(cookies).anyMatch(cookie -> cookie.startsWith("refreshToken="));
                }
        );
    }

    @DisplayName("로그인중인 사용자는 로그아웃을 하면 쿠키에 존재하던 accessToken과 refreshToken이 사라지고 빈 값으로 채워진다.")
    @Test
    void logout() {
        //given
        Member savedMember = memberRepository.save(FixtureUtil.getTestMentee());

        String accessToken = jwtProvider.createAccessToken(savedMember.getId(), savedMember.getRole());
        String refreshToken = jwtProvider.createRefreshToken();
        refreshTokenRepository.save(new RefreshToken(refreshToken, LocalDateTime.now(), savedMember));

        //when
        Response response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/post-logout-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .summary("로그아웃")
                                .description("로그아웃을 진행하고 쿠키의 토큰을 만료시킵니다. 성공 시 204 No Content를 반환합니다.")
                                .build())))
                .cookie("accessToken", accessToken)
                .cookie("refreshToken", refreshToken)
                .log().all().contentType(ContentType.JSON)
                .when()
                .post("/logout");

        // then
        List<String> cookies = response.getHeaders().getValues("Set-Cookie");

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(204);
            softly.assertThat(cookies).anyMatch(cookie ->
                    cookie.startsWith("accessToken=;")
                            && cookie.contains("Max-Age=0")
                            && cookie.contains("Path=/")
                            && cookie.contains("SameSite=None")
                            && cookie.contains("HttpOnly")
                            && cookie.contains("Secure"));
            softly.assertThat(cookies).anyMatch(cookie ->
                    cookie.startsWith("refreshToken=;")
                            && cookie.contains("Max-Age=0")
                            && cookie.contains("Path=/")
                            && cookie.contains("SameSite=None")
                            && cookie.contains("HttpOnly")
                            && cookie.contains("Secure"));
        });
    }

    @DisplayName("로그인 상태 요청 - accessToken이 존재하면 true와 사용자 id를 반환한다.")
    @Test
    void isLoggedIn() {
        //given
        Member savedMember = memberRepository.save(FixtureUtil.getTestMentee());
        String accessToken = jwtProvider.createAccessToken(savedMember.getId(), savedMember.getRole());

        //when
        //then
        LoginStatusDto response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/get-isLoggedIn-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .summary("로그인 상태 확인")
                                .description("현재 로그인 상태인지 확인합니다. 성공 시 200 OK, 실패 시 204 No Content를 반환합니다.")
                                .responseSchema(Schema.schema("LoginStatusDto"))
                                .responseFields(
                                        fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 ID")
                                )
                                .build())))
                .cookie("accessToken", accessToken)
                .log().all().contentType(ContentType.JSON)
                .when()
                .get("/auth/check")
                .then()
                .statusCode(200)
                .extract()
                .as(LoginStatusDto.class);

        assertThat(response.memberId()).isEqualTo(savedMember.getId());
    }

    @DisplayName("로그인 상태 요청 - accessToken이 존재하지 않으면 false와 null을 반환한다.")
    @Test
    void isLoggedIn2() {
        //given
        //when
        //then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/get-isLoggedIn-noAccessToken",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .when()
                .get("/auth/check")
                .then()
                .statusCode(204);
    }

    @DisplayName("토큰을 재발급 하면 상태코드 200을 응답하고, 새로운 accessToken과 refreshToken을 쿠키에 저장한다.")
    @Test
    void reissue() {
        //given
        Member member = new Member(
                "loginId",
                Gender.MALE,
                "이름",
                new Phone("010-1234-5678"),
                Password.from("password")
        );
        Member savedMember = memberRepository.save(member);
        String accessToken = jwtProvider.createAccessToken(savedMember.getId(), savedMember.getRole());
        String refreshToken = jwtProvider.createRefreshToken();

        refreshTokenRepository.save(new RefreshToken(refreshToken, LocalDateTime.now(), savedMember));

        //when
        Response reissueResponse = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/post-reissue-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .summary("토큰 재발급")
                                .description(
                                        "RefreshToken을 이용하여 AccessToken과 RefreshToken을 재발급합니다. 성공 시 200 OK, 실패 시 401 Unauthorized를 반환합니다.")
                                .build())))
                .log().all()
                .cookie("accessToken", accessToken)
                .cookie("refreshToken", refreshToken)
                .when()
                .post("/reissue");

        //then
        List<String> reissueCookies = reissueResponse.getHeaders().getValues("Set-Cookie");

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(reissueResponse.statusCode()).isEqualTo(200);
            softly.assertThat(reissueCookies).anyMatch(cookie -> cookie.startsWith("accessToken="));
            softly.assertThat(reissueCookies).anyMatch(cookie -> cookie.startsWith("refreshToken="));
        });
    }

    @DisplayName("토큰을 재발급 할 때, 유효하지 않은(존재하지 않는) refreshToken을 제공하면 401 상태코드를 받는다.")
    @Test
    void reissue2() {
        //given
        String accessToken = jwtProvider.createAccessToken(1L, MemberRole.MENTOR);
        String refreshToken = jwtProvider.createRefreshToken();

        //when
        Response reissueResponse = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/post-reissue-invalid-token",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all()
                .cookie("accessToken", accessToken)
                .cookie("refreshToken", refreshToken)
                .when()
                .post("/reissue");

        //then
        assertThat(reissueResponse.statusCode()).isEqualTo(401);
    }

    @DisplayName("토큰 없이 재발급을 요청할 경우 401 상태코드를 받는다.")
    @Test
    void reissue3() {
        //given
        //when
        Response reissueResponse = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/post-reissue-no-token",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all()
                .when()
                .post("/reissue");

        //then
        assertThat(reissueResponse.statusCode())
                .isEqualTo(401);
    }

    @DisplayName("사용자는 중복된 아이디로 회원가입을 할 수 없다.")
    @Test
    void signUp4() {
        //given
        Member member = new Member(
                "loginId",
                Gender.MALE,
                "이름",
                new Phone("010-1234-5678"),
                Password.from("password")
        );

        memberRepository.save(member);

        String loginId = "loginId";
        String name = "이름";
        Gender gender = Gender.MALE;
        String phoneNumber = "010-1234-5678";
        String password = "password";
        SignUpRequest request = new SignUpRequest(loginId, name, gender, phoneNumber, password);

        //when
        Response response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/post-signup-duplicated-login-id",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .requestSchema(Schema.schema("SignUpRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
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
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/post-validate-id-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .summary("아이디 중복 확인")
                                .description("아이디 중복 여부를 확인합니다. 성공 시 200 OK, 실패 시 400 Bad Request를 반환합니다.")
                                .requestSchema(Schema.schema("ValidateDuplicateLoginIdRequest"))
                                .requestFields(
                                        fieldWithPath("loginId").type(JsonFieldType.STRING).description("확인할 아이디")
                                )
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .when()
                .body(request)
                .post("/validate-login-id");

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
                        Gender.MALE,
                        "이름",
                        new Phone("010-1234-5678"),
                        Password.from("password")
                )
        );

        memberRepository.save(
                new Member(
                        "LoginId",
                        Gender.MALE,
                        "이름",
                        new Phone("010-5678-9123"),
                        Password.from("password")
                )
        );

        ValidateDuplicateLoginIdRequest request = new ValidateDuplicateLoginIdRequest("uniqueLoginId");

        //when
        Response response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/post-validate-id-duplicated",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .requestSchema(Schema.schema("ValidateDuplicateLoginIdRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .when()
                .body(request)
                .post("/validate-login-id");

        //then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @DisplayName("사용자는 유효한 전화번호 형식으로 인증을 요청하면 200 상태코드를 받는다.")
    @Test
    void requestAuthCode() {
        // given
        String phoneNumber = "010-1234-5678";
        VerifyPhoneNumberRequest request = new VerifyPhoneNumberRequest(phoneNumber);

        doNothing().when(smsRestClientService).sendSms(any(), anyString());

        // when
        // then
        RestAssured.given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/post-auth-code-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .summary("전화번호 인증 요청")
                                .description("전화번호 인증 코드를 요청합니다. 성공 시 201 Create, 실패 시 400 Bad Request를 반환합니다.")
                                .requestSchema(Schema.schema("VerifyPhoneNumberRequest"))
                                .requestFields(
                                        fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("전화번호")
                                )
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .when()
                .body(request)
                .when()
                .post("/auth-code")
                .then()
                .log().all()
                .statusCode(201);
    }

    @DisplayName("사용자는 잘못된 전화번호 형식으로 인증을 요청하면 400 상태코드를 받는다.")
    @Test
    void invalidPhoneNumberVerification() {
        // given
        String nonHyphenPhone = "01012345678";
        VerifyPhoneNumberRequest request = new VerifyPhoneNumberRequest(nonHyphenPhone);

        // when
        // then
        RestAssured.given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/post-auth-code-invalid-phoneNumber",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .requestSchema(Schema.schema("VerifyPhoneNumberRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .when()
                .body(request)
                .when()
                .post("/auth-code")
                .then()
                .log().all()
                .statusCode(400);
    }

    @DisplayName("사용자는 잘못된 코드로 인증을 요청하면 400 응답을 받는다.")
    @Test
    void invalidCodeVerification() {
        // given
        Phone phone = new Phone("010-1234-5678");
        String code = "123456";
        PhoneVerification phoneVerification = new PhoneVerification(phone, code, LocalDateTime.now().plusMinutes(3));
        phoneVerificationRepository.save(phoneVerification);
        VerificationCodeRequest request = new VerificationCodeRequest(phone.getNumber(), "invalidCode");

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/post-auth-code-verify-invalid-code",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .requestSchema(Schema.schema("VerificationCodeRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .when()
                .body(request)
                .when()
                .post("/auth-code/verify")
                .then()
                .log().all()
                .statusCode(400);
    }

    @DisplayName("사용자는 만료된 코드로 인증을 요청하면 400 응답을 받는다.")
    @Test
    void expiredCodeVerification() {
        // given
        Phone phone = new Phone("010-1234-5678");
        String code = "123456";
        PhoneVerification phoneVerification = new PhoneVerification(phone, code, LocalDateTime.now().minusMinutes(3));
        phoneVerificationRepository.save(phoneVerification);
        VerificationCodeRequest request = new VerificationCodeRequest(phone.getNumber(), code);

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/post-auth-code-verify-expired-code",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .requestSchema(Schema.schema("VerificationCodeRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .when()
                .body(request)
                .when()
                .post("/auth-code/verify")
                .then()
                .log().all()
                .statusCode(400);
    }

    @DisplayName("사용자는 유효한 인증을 요청하면 200 응답을 받는다.")
    @Test
    void validCodeVerification() {
        // given
        Phone phone = new Phone("010-1234-5678");
        String code = "123456";
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        PhoneVerification phoneVerification = new PhoneVerification(phone, code, now.plusMinutes(3));
        phoneVerificationRepository.save(phoneVerification);
        VerificationCodeRequest request = new VerificationCodeRequest(phone.getNumber(), code);

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/post-auth-code-verify-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .summary("인증 코드 확인")
                                .description("전화번호 인증 코드를 확인합니다. 성공 시 200 OK, 실패 시 400 Bad Request를 반환합니다.")
                                .requestSchema(Schema.schema("VerificationCodeRequest"))
                                .requestFields(
                                        fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("전화번호"),
                                        fieldWithPath("code").type(JsonFieldType.STRING).description("인증 코드")
                                )
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .when()
                .body(request)
                .when()
                .post("/auth-code/verify")
                .then()
                .log().all()
                .statusCode(200);
    }

    @DisplayName("사용자가 이름과 전화번호로 아이디를 찾으면 200 OK를 반환한다.")
    @Test
    void findLoginId() {
        // given
        String loginId = "loginId";
        String name = "이름";
        String phoneNumber = "010-1234-5678";
        Member member = new Member(
                loginId,
                Gender.MALE,
                name,
                new Phone(phoneNumber),
                Password.from("password")
        );
        memberRepository.save(member);

        FindLoginIdRequest request = new FindLoginIdRequest(name, phoneNumber);

        // when
        LoginIdResponse actual = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/get-login-id-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .summary("아이디 찾기")
                                .description("이름과 전화번호로 아이디를 찾습니다. 성공 시 200 OK, 실패 시 404 Not Found를 반환합니다.")
                                .requestSchema(Schema.schema("FindLoginIdRequest"))
                                .requestFields(
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                        fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("전화번호")
                                )
                                .responseSchema(Schema.schema("LoginIdResponse"))
                                .responseFields(
                                        fieldWithPath("loginId").type(JsonFieldType.STRING).description("찾은 아이디")
                                )
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .when()
                .body(request)
                .post("/login-id")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .as(LoginIdResponse.class);

        // then
        assertThat(actual.loginId()).isEqualTo(loginId);
    }

    @DisplayName("사용자가 존재하지 않는 정보로 아이디를 찾으면 404 Not Found를 반환한다.")
    @Test
    void findLoginIdFail() {
        // given
        FindLoginIdRequest request = new FindLoginIdRequest("없는이름", "010-0000-0000");

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/get-login-id-fail",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .requestSchema(Schema.schema("FindLoginIdRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .when()
                .body(request)
                .post("/login-id")
                .then()
                .log().all()
                .statusCode(404);
    }

    @DisplayName("사용자가 아이디와 전화번호로 비밀번호를 재설정하면 204 No Content를 반환한다.")
    @Test
    void resetPassword() {
        // given
        String loginId = "loginId";
        String name = "이름";
        String phoneNumber = "010-1234-5678";
        String oldPassword = "oldPassword";
        Member member = new Member(
                loginId,
                Gender.MALE,
                name,
                new Phone(phoneNumber),
                Password.from(oldPassword)
        );
        memberRepository.save(member);

        phoneVerificationRepository.save(
                FixtureUtil.getVerifiedPhoneVerification(new Phone(phoneNumber))
        );

        String newPassword = "newPassword";
        ResetPasswordRequest request = new ResetPasswordRequest(loginId, phoneNumber, newPassword);

        // when
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/post-reset-password-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .summary("비밀번호 재설정")
                                .description(
                                        "아이디와 전화번호 인증 후 비밀번호를 재설정합니다. 성공 시 204 No Content, 실패 시 400 Bad Request 또는 404 Not Found를 반환합니다.")
                                .requestSchema(Schema.schema("ResetPasswordRequest"))
                                .requestFields(
                                        fieldWithPath("loginId").type(JsonFieldType.STRING).description("아이디"),
                                        fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("전화번호"),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("새로운 비밀번호")
                                )
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .when()
                .body(request)
                .post("/reset-password")
                .then()
                .log().all()
                .statusCode(204);

        // then
        Member updatedMember = memberRepository.findByLoginId(loginId).orElseThrow();
        assertThatCode(() -> updatedMember.getPassword().validateMatches(newPassword))
                .doesNotThrowAnyException();
    }

    @DisplayName("사용자가 전화번호 인증 없이 비밀번호를 재설정하면 400 bad request를 반환한다.")
    @Test
    void resetPasswordFail_NoVerification() {
        // given
        String loginId = "loginId";
        String name = "이름";
        String phoneNumber = "010-1234-5678";
        String oldPassword = "oldPassword";
        Member member = new Member(
                loginId,
                Gender.MALE,
                name,
                new Phone(phoneNumber),
                Password.from(oldPassword)
        );
        memberRepository.save(member);

        String newPassword = "newPassword";
        ResetPasswordRequest request = new ResetPasswordRequest(loginId, phoneNumber, newPassword);

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/post-reset-password-fail-no-verification",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .requestSchema(Schema.schema("ResetPasswordRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .when()
                .body(request)
                .post("/reset-password")
                .then()
                .log().all()
                .statusCode(400);
    }

    @DisplayName("사용자가 일치하지 않는 정보로 비밀번호를 재설정하면 404 not found를 반환한다.")
    @Test
    void resetPasswordFail_InvalidInfo() {
        // given
        String loginId = "loginId";
        String name = "이름";
        String phoneNumber = "010-1234-5678";
        String oldPassword = "oldPassword";
        Member member = new Member(
                loginId,
                Gender.MALE,
                name,
                new Phone(phoneNumber),
                Password.from(oldPassword)
        );
        memberRepository.save(member);

        phoneVerificationRepository.save(
                FixtureUtil.getVerifiedPhoneVerification(new Phone(phoneNumber))
        );

        String newPassword = "newPassword";
        ResetPasswordRequest request = new ResetPasswordRequest("wrongLoginId", phoneNumber, newPassword);

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/post-reset-password-fail-invalid-info",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .requestSchema(Schema.schema("ResetPasswordRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .when()
                .body(request)
                .post("/reset-password")
                .then()
                .log().all()
                .statusCode(404);
    }

    @DisplayName("카카오 로그인 요청 시 카카오 인증 페이지로 리다이랙트되고, 302 Found를 반환한다.")
    @Test
    void redirectKakaoAuth() {
        // when
        // then
        RestAssured
                .given(spec)
                .redirects().follow(false) // 리다이랙트 자동 이동 방지
                .filter(documentWithTag("auth/get-kakao-login",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .summary("카카오 로그인 리다이렉트")
                                .description("카카오 로그인 페이지로 리다이렉트합니다. 성공 시 302 Found를 반환합니다.")
                                .build())))
                .log().all()
                .when()
                .get("/kakao/login")
                .then()
                .log().all()
                .statusCode(302)
                .header("Location", containsString("https://kauth.kakao.com/oauth/authorize"))
                .header("Location", containsString("client_id="))
                .header("Location", containsString("redirect_uri="))
                .header("Location", containsString("response_type=code"));
    }

    @DisplayName("OAuth 회원가입을 성공하면 201 Created와 토큰을 반환한다.")
    @Test
    void oauthSignUp() {
        // given
        String oauthId = "123456789";
        String oauthSignUpToken = jwtProvider.createOauthSignUpToken(oauthId);
        OauthSignUpRequest request = new OauthSignUpRequest("이름", Gender.MALE, "010-1234-5678");

        // when
        Response response = RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/post-oauth-signup-success",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .summary("OAuth 회원가입")
                                .description(
                                        "OAuth 인증 후 추가 정보를 입력하여 회원가입을 완료합니다. 성공 시 201 Created, 실패 시 400 Bad Request 또는 401 Unauthorized를 반환합니다.")
                                .requestSchema(Schema.schema("OauthSignUpRequest"))
                                .requestFields(
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                        fieldWithPath("gender").type(JsonFieldType.STRING)
                                                .description("성별 (MALE, FEMALE)"),
                                        fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("전화번호")
                                )
                                .build())))
                .cookie("oauthSignUpToken", oauthSignUpToken)
                .log().all().contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/oauth-signup");

        // then
        List<String> cookies = response.getHeaders().getValues("Set-Cookie");

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(201);
            softly.assertThat(cookies).anyMatch(cookie -> cookie.startsWith("accessToken="));
            softly.assertThat(cookies).anyMatch(cookie -> cookie.startsWith("refreshToken="));
        });
    }

    @DisplayName("OAuth 회원가입 시 유효하지 않은 토큰이면 401 Unauthorized를 반환한다.")
    @Test
    void oauthSignUpFail_InvalidToken() {
        // given
        String invalidToken = "invalidToken";
        OauthSignUpRequest request = new OauthSignUpRequest("이름", Gender.MALE, "010-1234-5678");

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/post-oauth-signup-fail-invalid-token",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .requestSchema(Schema.schema("OauthSignUpRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .cookie("oauthSignUpToken", invalidToken)
                .log().all().contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/oauth-signup")
                .then()
                .log().all()
                .statusCode(401);
    }

    @DisplayName("OAuth 회원가입 시 유효하지 않은 정보가 포함되어 있으면 400 Bad Request를 반환한다.")
    @Test
    void oauthSignUpFail_InvalidInput() {
        // given
        String oauthId = "123456789";
        String oauthSignUpToken = jwtProvider.createOauthSignUpToken(oauthId);
        OauthSignUpRequest request = new OauthSignUpRequest("", null, "invalid-phoneNumber");

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("auth/post-oauth-signup-fail-invalid-input",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .requestSchema(Schema.schema("OauthSignUpRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .cookie("oauthSignUpToken", oauthSignUpToken)
                .log().all().contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/oauth-signup")
                .then()
                .log().all()
                .statusCode(400);
    }

    @DisplayName("카카오 로그인 콜백 - 기존 회원이면 로그인 성공 후 메인 페이지로 리다이랙트된다.")
    @Test
    void kakaoCallBack_LoginSuccess() {
        // given
        String code = "authCode";
        String state = jwtProvider.createStateToken();
        String kakaoAccessToken = "kakaoAccessToken";
        Long kakaoId = 12345L;

        // 기존 회원 생성 및 연동
        Member member = memberRepository.save(FixtureUtil.getTestMentee());
        memberOauthRepository.save(new MemberOauth(member, AuthProvider.KAKAO, String.valueOf(kakaoId)));

        // Mocking
        when(oauthClientService.requestKakaoToken(code))
                .thenReturn(
                        new KakaoTokenResponse(
                                kakaoAccessToken,
                                3600,
                                "refreshToken",
                                3600
                        ));
        when(oauthClientService.requestKakaoId(kakaoAccessToken))
                .thenReturn(new KakaoUserInfoResponse(kakaoId));

        // when
        Response response = RestAssured
                .given(spec)
                .redirects().follow(false)
                .filter(documentWithTag("auth/get-kakao-callback-login",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .summary("카카오 로그인 콜백")
                                .description(
                                        "카카오 로그인 성공 시 메인 페이지로 리다이렉트합니다. 성공 시 302 Found, 실패 시 400 Bad Request를 반환합니다.")
                                .build())))
                .queryParam("code", code)
                .queryParam("state", state)
                .log().all()
                .when()
                .get("/kakao/callback");

        // then
        List<String> cookies = response.getHeaders().getValues("Set-Cookie");

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(302);
            softly.assertThat(response.getHeader("Location")).contains("http://localhost:3000"); // client.base-url
            softly.assertThat(cookies).anyMatch(cookie -> cookie.startsWith("accessToken="));
            softly.assertThat(cookies).anyMatch(cookie -> cookie.startsWith("refreshToken="));
        });
    }

    @DisplayName("카카오 로그인 콜백 - 신규 회원이면 회원가입 페이지로 리다이랙트된다.")
    @Test
    void kakaoCallBack_SignUp() {
        // given
        String code = "authCode";
        String state = jwtProvider.createStateToken();
        String kakaoAccessToken = "kakaoAccessToken";
        Long kakaoId = 67890L;

        // Mocking
        when(oauthClientService.requestKakaoToken(code))
                .thenReturn(
                        new KakaoTokenResponse(
                                kakaoAccessToken,
                                3600,
                                "refreshToken",
                                3600
                        ));
        when(oauthClientService.requestKakaoId(kakaoAccessToken))
                .thenReturn(new KakaoUserInfoResponse(kakaoId));

        // when
        Response response = RestAssured
                .given(spec)
                .redirects().follow(false)
                .filter(documentWithTag("auth/get-kakao-callback-signup",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .build())))
                .queryParam("code", code)
                .queryParam("state", state)
                .log().all()
                .when()
                .get("/kakao/callback");

        // then
        List<String> cookies = response.getHeaders().getValues("Set-Cookie");

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(302);
            softly.assertThat(response.getHeader("Location")).contains("/identity-verification");
            softly.assertThat(cookies).anyMatch(cookie -> cookie.startsWith("oauthSignUpToken="));
        });
    }

    @DisplayName("카카오 로그인 콜백 - 외부 API 호출 실패 시 400 Bad Request를 반환한다.")
    @Test
    void kakaoCallBack_Fail() {
        // given
        String code = "authCode";
        String state = jwtProvider.createStateToken();

        // Mocking - 예외 발생
        when(oauthClientService.requestKakaoToken(code))
                .thenThrow(new RuntimeException("Kakao API Error"));

        // when
        // then
        RestAssured
                .given(spec)
                .redirects().follow(false)
                .filter(documentWithTag("auth/get-kakao-callback-fail",
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증")
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .queryParam("code", code)
                .queryParam("state", state)
                .log().all()
                .when()
                .get("/kakao/callback")
                .then()
                .log().all()
                .statusCode(400)
                .body("message", equalTo(BusinessErrorMessage.KAKAO_LOGIN_FAILED.getMessage()));
    }
}
