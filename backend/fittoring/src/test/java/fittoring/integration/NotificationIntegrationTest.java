package fittoring.integration;

import fittoring.AbstractApiDocumentationTest;
import fittoring.application.FixtureUtil;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.notification.presentation.dto.request.PushTokenUpsertRequest;
import fittoring.domain.model.Member;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class NotificationIntegrationTest extends AbstractApiDocumentationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @DisplayName("FCM 토큰이 저장되지 않은 유저가 FCM 토큰 업서트 요청 시 200 OK를 반환한다.")
    @Test
    void upsertFcmToken1() {
        // given
        Member member = memberRepository.save(FixtureUtil.getTestMentee());
        String accessToken = jwtProvider.createAccessToken(member.getId());
        String hardwareId = "hardwareIdhardwareIdhardwareIdhardwareId";
        String token = "testFcmTokentestFcmTokentestFcmToken";
        PushTokenUpsertRequest request = new PushTokenUpsertRequest(member.getId(), hardwareId, token);

        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("reservation/post-upsert-fcm-token-success-1"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(request)
                .when()
                .post("/notification/tokens")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("FCM 토큰이 저장된 유저가 FCM 토큰 업서트 요청 시 FCM 토큰 업서트 요청 시 200 OK를 반환한다.")
    @Test
    void upsertFcmToken2() {
        // given
        Member member = memberRepository.save(FixtureUtil.getTestMentee());
        String accessToken = jwtProvider.createAccessToken(member.getId());
        String hardwareId = "hardwareIdhardwareIdhardwareIdhardwareId";
        String originalToken = "testFcmTokentestFcmTokentestFcmToken";
        String newToken = "testFcmTokentestFcmTokentestFcmToken";
        PushTokenUpsertRequest originalRequest = new PushTokenUpsertRequest(member.getId(), hardwareId, originalToken);
        PushTokenUpsertRequest newRequest = new PushTokenUpsertRequest(member.getId(), hardwareId, newToken);

        RestAssured
                .given(spec)
                .accept("application/json")
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(originalRequest)
                .when()
                .post("/notification/tokens")
                .then().log().all()
                .statusCode(200);

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

    @DisplayName("존재하지 않는 유저가 FCM 토큰 업서트 요청 시 404 Not Found를 반환한다.")
    @Test
    void upsertFcmTokenFail1() {
        // given
        Long invalidMemberId = 999L;
        String accessToken = jwtProvider.createAccessToken(invalidMemberId);
        String hardwareId = "hardwareIdhardwareIdhardwareIdhardwareId";
        String token = "testFcmTokentestFcmTokentestFcmToken";
        PushTokenUpsertRequest request = new PushTokenUpsertRequest(invalidMemberId, hardwareId, token);

        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("reservation/post-upsert-fcm-token-success-1"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(request)
                .when()
                .post("/notification/tokens")
                .then().log().all()
                .statusCode(404);
    }
}
