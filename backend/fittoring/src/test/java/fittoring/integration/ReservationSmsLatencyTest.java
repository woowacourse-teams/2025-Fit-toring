package fittoring.integration;

import static org.mockito.Mockito.doAnswer;

import fittoring.AbstractApiDocumentationTest;
import fittoring.application.FixtureUtil;
import fittoring.application.auth.service.JwtProvider;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.application.reservation.presentation.dto.request.ReservationCreateRequest;
import fittoring.domain.model.Member;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Phone;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Step 1 — 비동기 전환 요구사항을 명문화하는 SLA 테스트.
 *
 * 예약 API는 외부 SMS 호출 지연과 무관하게 정해진 응답 예산(1500ms) 안에 응답해야 한다.
 *
 * - 현재 동기 구조에서는 SMS 호출이 요청 스레드를 점유하므로 이 테스트는 Red(fail)이다.
 *   동기 결합이 응답 시간으로 전이된다는 사실을 fail 메시지가 그대로 증언한다.
 * - Step 2 (@Async + @TransactionalEventListener AFTER_COMMIT) 전환이 완료되면
 *   같은 테스트가 변경 없이 Green으로 뒤집힌다.
 */
class ReservationSmsLatencyTest extends AbstractApiDocumentationTest {

    private static final long INJECTED_SMS_DELAY_MS = 2_000L;
    private static final long RESPONSE_LIMIT_MS = 1_500L;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MentoringRepository mentoringRepository;

    @DisplayName("예약 API는 SMS 외부 호출 지연과 무관하게 응답 예산(1500ms) 안에 응답해야 한다.")
    @Test
    void reservationResponseMustNotBlockOnSmsLatency() throws Exception {

        // given: SMS 클라이언트가 2초간 블로킹되도록 stubbing
        doAnswer(invocation -> {
            Thread.sleep(INJECTED_SMS_DELAY_MS);
            return null;
        }).when(smsRestClientService)
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

        // warm-up: cold-start 변동성(JIT, Hikari, 필터 체인, RestAssured)을 제거하기 위해
        // SMS mock과 무관한 GET을 한 번 흘려보낸다. 요청이 필터/디스패처를 통과해 JVM이 데워지는 것 자체가 목적이다.
        RestAssured
                .given(spec)
                .when()
                .get("/mentorings-page");

        // when: 예약 API 호출 시각과 응답 수신 시각을 측정
        long startNanos = System.nanoTime();
        Response response = RestAssured
                .given(spec)
                .contentType(ContentType.JSON)
                .cookie("accessToken", accessToken)
                .body(request)
                .when()
                .post("/mentorings/{mentoringId}/reservation", mentoring.getId());
        long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000L;

        // then: 예약은 정상 응답되어야 하고, 응답 시간은 SMS 지연과 무관하게 예산 안에 들어와야 한다.
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(201);
            softly.assertThat(elapsedMs)
                    .as(
                            "SMS 지연 %dms와 무관하게 응답은 %dms 안에 반환되어야 한다 (실측 %dms)",
                            INJECTED_SMS_DELAY_MS,
                            RESPONSE_LIMIT_MS,
                            elapsedMs
                    )
                    .isLessThan(RESPONSE_LIMIT_MS);
        });
    }
}
