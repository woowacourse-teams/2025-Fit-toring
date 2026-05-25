package fittoring.integration;

import static org.mockito.Mockito.doThrow;

import fittoring.AbstractApiDocumentationTest;
import fittoring.application.FixtureUtil;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.application.reservation.presentation.dto.request.ReservationCreateRequest;
import fittoring.application.reservation.repository.ReservationRepository;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Phone;
import fittoring.domain.model.Reservation;
import fittoring.domain.model.Status;
import fittoring.infrastructure.exception.InfraErrorMessage;
import fittoring.infrastructure.exception.SmsException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;

class ReservationSmsFailureIntegrationTest extends AbstractApiDocumentationTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @DisplayName("외부 SMS API가 5xx로 실패해도 예약 API는 201을 응답하고 DB에 예약이 영속화된다.")
    @Test
    void smsFailureMustNotBreakReservation() {
        // given: SMS 클라이언트가 CoolSMS 5xx를 SmsException으로 변환해 던지는 상황을 재현
        doThrow(new SmsException(InfraErrorMessage.SMS_SERVER_ERROR.getMessage()))
                .when(smsRestClientService)
                .sendSms(
                        ArgumentMatchers.any(Phone.class),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString()
                );

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

        // then: SMS 실패와 무관하게 예약은 성공으로 응답되어야 하며, DB에도 정상 영속화되어야 한다.
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(201);
            softly.assertThat(reservationRepository.findAll()).hasSize(1);
        });
    }

    @DisplayName("외부 SMS API가 5xx로 실패해도 예약 승인 API는 200을 응답하고 DB의 예약 상태는 APPROVED가 된다.")
    @Test
    void smsFailureMustNotBreakApprove() {
        // given
        doThrow(new SmsException(InfraErrorMessage.SMS_SERVER_ERROR.getMessage()))
                .when(smsRestClientService)
                .sendSms(
                        ArgumentMatchers.any(Phone.class),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString()
                );

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
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(200);
            softly.assertThat(reservationRepository.findById(reservation.getId()))
                    .isPresent()
                    .hasValueSatisfying(r ->
                            softly.assertThat(r.getStatus()).isEqualTo(Status.APPROVED.name())
                    );
        });
    }

    @DisplayName("외부 SMS API가 5xx로 실패해도 예약 거절 API는 200을 응답하고 DB의 예약 상태는 REJECTED가 된다.")
    @Test
    void smsFailureMustNotBreakReject() {
        // given
        doThrow(new SmsException(InfraErrorMessage.SMS_SERVER_ERROR.getMessage()))
                .when(smsRestClientService)
                .sendSms(
                        ArgumentMatchers.any(Phone.class),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString()
                );

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
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(200);
            softly.assertThat(reservationRepository.findById(reservation.getId()))
                    .isPresent()
                    .hasValueSatisfying(r ->
                            softly.assertThat(r.getStatus()).isEqualTo(Status.REJECTED.name())
                    );
        });
    }
}
