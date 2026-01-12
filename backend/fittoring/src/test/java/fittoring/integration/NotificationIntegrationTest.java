package fittoring.integration;

import static org.hamcrest.Matchers.equalTo;

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
                .filter(documentWithTag("notification/post-register-device-success-1"))
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
                .filter(documentWithTag("notification/post-register-device-fail-1"))
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
