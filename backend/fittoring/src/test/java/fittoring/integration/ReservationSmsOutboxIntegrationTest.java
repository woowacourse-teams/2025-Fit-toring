package fittoring.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import fittoring.AbstractApiDocumentationTest;
import fittoring.application.FixtureUtil;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.application.reservation.presentation.dto.request.ReservationCreateRequest;
import fittoring.application.reservation.repository.ReservationRepository;
import fittoring.application.reservation.repository.SmsOutboxRepository;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Reservation;
import fittoring.domain.model.SmsOutbox;
import fittoring.domain.model.SmsOutboxEventType;
import fittoring.domain.model.SmsOutboxStatus;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

class ReservationSmsOutboxIntegrationTest extends AbstractApiDocumentationTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @MockitoSpyBean
    private SmsOutboxRepository smsOutboxRepository;

    @DisplayName("예약 생성이 커밋되면 sms_outbox에 PENDING row가 1건 영속화된다.")
    @Test
    void outboxRowPersistedAfterReservationCreated() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));

        String accessToken = jwtProvider.createAccessToken(mentee.getId(), mentee.getRole());
        ReservationCreateRequest request = new ReservationCreateRequest("멘토링 예약 내용");

        // when
        Response response = RestAssured
                .given(spec)
                .contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(request)
                .when()
                .post("/mentorings/{mentoringId}/reservation", mentoring.getId());

        // then
        List<SmsOutbox> outboxRows = smsOutboxRepository.findAll();
        List<Reservation> reservations = reservationRepository.findAll();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(201);
            softly.assertThat(reservations).hasSize(1);
            softly.assertThat(outboxRows).hasSize(1);
            SmsOutbox outbox = outboxRows.get(0);
            softly.assertThat(outbox.getEventType()).isEqualTo(SmsOutboxEventType.RESERVATION_CREATED);
            softly.assertThat(outbox.getStatus()).isEqualTo(SmsOutboxStatus.PENDING);
            softly.assertThat(outbox.getAttempts()).isZero();
            softly.assertThat(outbox.getToPhone()).isEqualTo(mentor.getPhone().getNumber());
            softly.assertThat(outbox.getReservationId()).isEqualTo(reservations.get(0).getId());
            softly.assertThat(outbox.getLastError()).isNull();
        });
    }

    @DisplayName("예약 승인이 커밋되면 sms_outbox에 APPROVED PENDING row가 1건 추가된다.")
    @Test
    void outboxRowPersistedAfterReservationApproved() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));
        Reservation reservation = reservationRepository.save(
                FixtureUtil.testPendingReservation(mentoring, mentee)
        );

        String accessToken = jwtProvider.createAccessToken(mentor.getId(), mentor.getRole());

        // when
        Response response = RestAssured
                .given(spec)
                .contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .patch("/reservations/{reservationId}/approve", reservation.getId());

        // then
        List<SmsOutbox> outboxRows = smsOutboxRepository.findAll();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(200);
            softly.assertThat(outboxRows).hasSize(1);
            SmsOutbox outbox = outboxRows.get(0);
            softly.assertThat(outbox.getEventType()).isEqualTo(SmsOutboxEventType.RESERVATION_APPROVED);
            softly.assertThat(outbox.getStatus()).isEqualTo(SmsOutboxStatus.PENDING);
            softly.assertThat(outbox.getToPhone()).isEqualTo(mentee.getPhone().getNumber());
            softly.assertThat(outbox.getReservationId()).isEqualTo(reservation.getId());
        });
    }

    @DisplayName("예약 거절이 커밋되면 sms_outbox에 REJECTED PENDING row가 1건 추가된다.")
    @Test
    void outboxRowPersistedAfterReservationRejected() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));
        Reservation reservation = reservationRepository.save(
                FixtureUtil.testPendingReservation(mentoring, mentee)
        );

        String accessToken = jwtProvider.createAccessToken(mentor.getId(), mentor.getRole());

        // when
        Response response = RestAssured
                .given(spec)
                .contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .when()
                .patch("/reservations/{reservationId}/reject", reservation.getId());

        // then
        List<SmsOutbox> outboxRows = smsOutboxRepository.findAll();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(200);
            softly.assertThat(outboxRows).hasSize(1);
            SmsOutbox outbox = outboxRows.get(0);
            softly.assertThat(outbox.getEventType()).isEqualTo(SmsOutboxEventType.RESERVATION_REJECTED);
            softly.assertThat(outbox.getStatus()).isEqualTo(SmsOutboxStatus.PENDING);
            softly.assertThat(outbox.getToPhone()).isEqualTo(mentee.getPhone().getNumber());
            softly.assertThat(outbox.getReservationId()).isEqualTo(reservation.getId());
        });
    }

    @DisplayName("outbox INSERT가 실패하면 같은 트랜잭션의 reservation도 롤백된다 (원자성).")
    @Test
    void outboxAndReservationRollbackAtomically() {
        // given: outbox 저장이 실패하도록 spy 스텁
        doThrow(new RuntimeException("outbox write failed"))
                .when(smsOutboxRepository).save(any(SmsOutbox.class));

        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        Mentoring mentoring = mentoringRepository.save(FixtureUtil.testMentoring(mentor));

        String accessToken = jwtProvider.createAccessToken(mentee.getId(), mentee.getRole());
        ReservationCreateRequest request = new ReservationCreateRequest("멘토링 예약 내용");

        // when
        Response response = RestAssured
                .given(spec)
                .contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(request)
                .when()
                .post("/mentorings/{mentoringId}/reservation", mentoring.getId());

        // then: 5xx 응답이지만, 핵심은 reservation도 함께 롤백되어야 한다는 것
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isGreaterThanOrEqualTo(500);
            softly.assertThat(reservationRepository.findAll())
                    .as("outbox 실패 시 reservation도 함께 롤백되어 0건이어야 한다")
                    .isEmpty();
        });
    }
}
