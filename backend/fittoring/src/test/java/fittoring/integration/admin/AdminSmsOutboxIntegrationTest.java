package fittoring.integration.admin;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verifyNoInteractions;

import fittoring.AbstractApiDocumentationTest;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.reservation.repository.SmsOutboxRepository;
import fittoring.domain.model.Gender;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberRole;
import fittoring.domain.model.Phone;
import fittoring.domain.model.SmsOutbox;
import fittoring.domain.model.SmsOutboxEventType;
import fittoring.domain.model.SmsOutboxStatus;
import fittoring.domain.model.password.Password;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AdminSmsOutboxIntegrationTest extends AbstractApiDocumentationTest {

    private static final String SUBJECT = "핏토링 예약 알림";
    private static final int MAX_ATTEMPTS = 3;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SmsOutboxRepository smsOutboxRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @DisplayName("관리자는 status 기본값(FAILED)으로 SMS outbox 목록을 조회할 수 있다.")
    @Test
    void findFailedRowsAsAdmin() {
        // given: FAILED row 2건 + PENDING row 1건 → 기본 조회는 FAILED만 노출
        smsOutboxRepository.save(failedRow("010-1111-1111"));
        smsOutboxRepository.save(failedRow("010-2222-2222"));
        smsOutboxRepository.save(pendingRow("010-3333-3333"));

        String adminAccessToken = adminAccessToken();

        // when //then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("admin/get-admin-sms-outbox-success"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", adminAccessToken)
                .when()
                .get("/admin/sms-outbox")
                .then().log().all()
                .statusCode(200)
                .body("content", hasSize(2))
                .body("content[0].status", equalTo(SmsOutboxStatus.FAILED.name()))
                .body("content[0].toPhone", equalTo("010-2222-****"));
    }

    @DisplayName("일반 사용자는 SMS outbox 목록을 조회할 수 없다.")
    @Test
    void findRequiresAdmin() {
        // given
        Member normal = memberRepository.save(new Member(
                "normalId",
                Gender.MALE,
                "일반",
                new Phone("010-9999-9999"),
                Password.from("password"),
                MemberRole.MENTEE
        ));
        String normalAccessToken = jwtProvider.createAccessToken(normal.getId(), normal.getRole());

        // when //then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("admin/get-admin-sms-outbox-unauthorized"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", normalAccessToken)
                .when()
                .get("/admin/sms-outbox")
                .then().log().all()
                .statusCode(403);
    }

    @DisplayName("관리자는 SMS outbox row의 상세 정보를 조회할 수 있다.")
    @Test
    void findDetailAsAdmin() {
        // given
        SmsOutbox row = smsOutboxRepository.save(failedRow("010-1111-1111"));
        String adminAccessToken = adminAccessToken();

        // when //then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("admin/get-admin-sms-outbox-id-success"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", adminAccessToken)
                .when()
                .get("/admin/sms-outbox/{id}", row.getId())
                .then().log().all()
                .statusCode(200)
                .body("id", is(row.getId().intValue()))
                .body("status", equalTo(SmsOutboxStatus.FAILED.name()))
                .body("message", equalTo("메시지 본문"));
    }

    @DisplayName("관리자가 FAILED row를 수동 재시도하면 PENDING으로 되돌아가고 직접 발송은 일어나지 않는다.")
    @Test
    void retryFailedRowRequeuesAsPending() {
        // given
        SmsOutbox row = smsOutboxRepository.save(failedRow("010-1111-1111"));
        String adminAccessToken = adminAccessToken();

        // when //then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("admin/post-admin-sms-outbox-id-retry-success"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", adminAccessToken)
                .when()
                .post("/admin/sms-outbox/{id}/retry", row.getId())
                .then().log().all()
                .statusCode(204);

        SmsOutbox refreshed = smsOutboxRepository.findById(row.getId()).orElseThrow();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(refreshed.getStatus()).isEqualTo(SmsOutboxStatus.PENDING);
            softly.assertThat(refreshed.getAttempts()).isZero();
            softly.assertThat(refreshed.getLastError()).isNull();
            softly.assertThat(refreshed.getProcessingStartedAt()).isNull();
            softly.assertThat(refreshed.getFailedNotifiedAt()).isNull();
        });
        verifyNoInteractions(smsRestClientService);
    }

    @DisplayName("FAILED가 아닌 row를 수동 재시도하면 409 Conflict를 반환한다.")
    @Test
    void retryNonFailedRowReturns409() {
        // given: PENDING row
        SmsOutbox row = smsOutboxRepository.save(pendingRow("010-1111-1111"));
        String adminAccessToken = adminAccessToken();

        // when //then
        RestAssured
                .given(spec)
                .accept("application/json")
                .filter(documentWithTag("admin/post-admin-sms-outbox-id-retry-conflict"))
                .log().all().contentType(ContentType.JSON)
                .cookie("accessToken", adminAccessToken)
                .when()
                .post("/admin/sms-outbox/{id}/retry", row.getId())
                .then().log().all()
                .statusCode(409);
    }

    private String adminAccessToken() {
        Member admin = memberRepository.save(new Member(
                "adminId",
                Gender.MALE,
                "관리자",
                new Phone("010-1111-2222"),
                Password.from("password"),
                MemberRole.ADMIN
        ));
        return jwtProvider.createAccessToken(admin.getId(), admin.getRole());
    }

    private SmsOutbox pendingRow(String toPhone) {
        return SmsOutbox.pending(
                1L,
                SmsOutboxEventType.RESERVATION_CREATED,
                new Phone(toPhone),
                "메시지 본문",
                SUBJECT
        );
    }

    private SmsOutbox failedRow(String toPhone) {
        SmsOutbox row = pendingRow(toPhone);
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            row.markProcessing(java.time.LocalDateTime.now());
            row.recordFailure("발송 실패", MAX_ATTEMPTS);
        }
        return row;
    }
}
