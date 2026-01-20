package fittoring.integration;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import fittoring.AbstractApiDocumentationTest;
import fittoring.application.FixtureUtil;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.notification.presentation.dto.request.RegisterDeviceRequest;
import fittoring.application.notification.repository.DeviceRepository;
import fittoring.domain.model.Device;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberRole;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.JsonFieldType;

class NotificationIntegrationTest extends AbstractApiDocumentationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @DisplayName("디바이스 등록 성공 시 200 OK를 반환한다.")
    @Test
    void registerDevice1() {
        // given
        Member member = memberRepository.save(FixtureUtil.getTestMentee());
        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());
        String token = "testpushtokentestpushtokentestpushtoken";
        RegisterDeviceRequest request = new RegisterDeviceRequest(member.getId(), token);

        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("notification/post-register-device-success-1",
                        resource(ResourceSnippetParameters.builder()
                                .tag("알림")
                                .summary("디바이스 등록")
                                .description("푸시 알림을 위한 디바이스 토큰을 등록합니다. 성공 시 200 OK, 실패 시 404 Not Found 또는 409 Conflict를 반환합니다.")
                                .requestSchema(Schema.schema("RegisterDeviceRequest"))
                                .requestFields(
                                        fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 ID"),
                                        fieldWithPath("pushToken").type(JsonFieldType.STRING).description("푸시 토큰")
                                )
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(request)
                .when()
                .post("/notification/tokens")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("푸시 토큰이 다른 디바이스를 추가로 등록할 수 있다.")
    @Test
    void registerDevice2() {
        // given
        Member member = memberRepository.save(FixtureUtil.getTestMentee());
        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());
        String originalToken = "testpushtokentestpushtokentestpushtoken";
        String newToken = "newtestpushtokennewtestpushtokennewtest";
        RegisterDeviceRequest newRequest = new RegisterDeviceRequest(member.getId(), newToken);

        deviceRepository.save(new Device(member, originalToken));

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("notification/post-register-device-success-multiple",
                        resource(ResourceSnippetParameters.builder()
                                .tag("알림")
                                .summary("디바이스 추가 등록")
                                .description("새로운 디바이스 토큰을 추가로 등록합니다.")
                                .requestSchema(Schema.schema("RegisterDeviceRequest"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(newRequest)
                .when()
                .post("/notification/tokens")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("존재하지 않는 유저가 디바이스 등록 요청 시 404 Not Found를 반환한다.")
    @Test
    void registerDeviceFail1() {
        // given
        Long invalidMemberId = 999L;
        String accessToken = jwtProvider.createAccessToken(invalidMemberId, MemberRole.MENTEE);
        String token = "testpushtokentestpushtokentestpushtoken";
        RegisterDeviceRequest request = new RegisterDeviceRequest(invalidMemberId, token);

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("notification/post-register-device-fail-not-found-user",
                        resource(ResourceSnippetParameters.builder()
                                .tag("알림")
                                .requestSchema(Schema.schema("RegisterDeviceRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(request)
                .when()
                .post("/notification/tokens")
                .then().log().all()
                .statusCode(404);
    }

    @DisplayName("이미 등록된 푸시 토큰으로 새 디바이스 등록 요청 시 400 예외가 발생한다.")
    @Test
    void registerDeviceFail2() {
        // given
        Member member = memberRepository.save(FixtureUtil.getTestMentee());
        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());
        String pushToken = "testpushtokentestpushtokentestpushtoken";
        RegisterDeviceRequest request = new RegisterDeviceRequest(member.getId(), pushToken);
        deviceRepository.save(new Device(member, pushToken));

        // when
        // then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("notification/post-upsert-register-device-fail-duplicate-device",
                        resource(ResourceSnippetParameters.builder()
                                .tag("알림")
                                .requestSchema(Schema.schema("RegisterDeviceRequest"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .build())))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(request)
                .when()
                .post("/notification/tokens")
                .then().log().all()
                .statusCode(409)
                .body("message", equalTo(BusinessErrorMessage.ALREADY_REGISTERED_DEVICE.getMessage()));
    }
}
