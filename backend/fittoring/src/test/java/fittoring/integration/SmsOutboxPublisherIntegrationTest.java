package fittoring.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import fittoring.AbstractApiDocumentationTest;
import fittoring.application.reservation.repository.SmsOutboxRepository;
import fittoring.infrastructure.SmsOutboxPublisher;
import fittoring.infrastructure.SmsOutboxResultApplier;
import fittoring.domain.model.Phone;
import fittoring.domain.model.SmsOutbox;
import fittoring.domain.model.SmsOutboxEventType;
import fittoring.domain.model.SmsOutboxStatus;
import fittoring.infrastructure.exception.InfraErrorMessage;
import fittoring.infrastructure.exception.SmsException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SmsOutboxPublisherIntegrationTest extends AbstractApiDocumentationTest {

    private static final String SUBJECT = "핏토링 예약 알림";

    @Autowired
    private SmsOutboxRepository smsOutboxRepository;

    @Autowired
    private SmsOutboxPublisher smsOutboxPublisher;

    @DisplayName("PENDING row가 SMS 발송 성공 시 SENT로 전환된다.")
    @Test
    void pendingRowMarkedAsSentOnSuccess() {
        // given
        doNothing().when(smsRestClientService).sendSms(any(Phone.class), anyString(), anyString());
        SmsOutbox row = smsOutboxRepository.save(SmsOutbox.pending(
                1L,
                SmsOutboxEventType.RESERVATION_CREATED,
                new Phone("010-0000-0001"),
                "메시지 본문",
                SUBJECT
        ));

        // when
        smsOutboxPublisher.publishPending();

        // then
        SmsOutbox refreshed = smsOutboxRepository.findById(row.getId()).orElseThrow();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(refreshed.getStatus()).isEqualTo(SmsOutboxStatus.SENT);
            softly.assertThat(refreshed.getAttempts()).isZero();
            softly.assertThat(refreshed.getLastError()).isNull();
        });
    }

    @DisplayName("PENDING row가 SMS 발송 실패 시 attempts=1 + 마지막 에러를 기록하고 status는 PENDING으로 남는다.")
    @Test
    void singleFailureKeepsRowPendingAndIncrementsAttempts() {
        // given
        doThrow(new SmsException(InfraErrorMessage.SMS_SERVER_ERROR.getMessage()))
                .when(smsRestClientService)
                .sendSms(any(Phone.class), anyString(), anyString());
        SmsOutbox row = smsOutboxRepository.save(SmsOutbox.pending(
                1L,
                SmsOutboxEventType.RESERVATION_CREATED,
                new Phone("010-0000-0001"),
                "메시지 본문",
                SUBJECT
        ));

        // when
        smsOutboxPublisher.publishPending();

        // then
        SmsOutbox refreshed = smsOutboxRepository.findById(row.getId()).orElseThrow();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(refreshed.getStatus()).isEqualTo(SmsOutboxStatus.PENDING);
            softly.assertThat(refreshed.getAttempts()).isEqualTo(1);
            softly.assertThat(refreshed.getLastError())
                    .isEqualTo(InfraErrorMessage.SMS_SERVER_ERROR.getMessage());
        });
    }

    @DisplayName("MAX_ATTEMPTS(3)회 연속 실패 시 status는 FAILED로 전환되고 마지막 에러가 기록된다.")
    @Test
    void maxedFailuresMarkRowAsFailed() {
        // given
        doThrow(new SmsException(InfraErrorMessage.SMS_SERVER_ERROR.getMessage()))
                .when(smsRestClientService)
                .sendSms(any(Phone.class), anyString(), anyString());
        SmsOutbox row = smsOutboxRepository.save(SmsOutbox.pending(
                1L,
                SmsOutboxEventType.RESERVATION_CREATED,
                new Phone("010-0000-0001"),
                "메시지 본문",
                SUBJECT
        ));

        // when: 3회 폴링 (실패 누적)
        for (int i = 0; i < SmsOutboxResultApplier.MAX_ATTEMPTS; i++) {
            smsOutboxPublisher.publishPending();
        }

        // then
        SmsOutbox refreshed = smsOutboxRepository.findById(row.getId()).orElseThrow();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(refreshed.getStatus()).isEqualTo(SmsOutboxStatus.FAILED);
            softly.assertThat(refreshed.getAttempts()).isEqualTo(SmsOutboxResultApplier.MAX_ATTEMPTS);
            softly.assertThat(refreshed.getLastError())
                    .isEqualTo(InfraErrorMessage.SMS_SERVER_ERROR.getMessage());
        });
    }

    @DisplayName("FAILED row는 더 이상 폴링 대상이 아니다.")
    @Test
    void failedRowsAreNotPicked() {
        // given: 처음부터 PENDING이지만 attempts가 이미 maxed 직전인 row를 직접 만들지 않고,
        //        FAILED로 진입한 후 추가 폴링 호출 시 retry가 없어야 함을 검증
        doThrow(new SmsException(InfraErrorMessage.SMS_SERVER_ERROR.getMessage()))
                .when(smsRestClientService)
                .sendSms(any(Phone.class), anyString(), anyString());
        SmsOutbox row = smsOutboxRepository.save(SmsOutbox.pending(
                1L,
                SmsOutboxEventType.RESERVATION_CREATED,
                new Phone("010-0000-0001"),
                "메시지 본문",
                SUBJECT
        ));
        for (int i = 0; i < SmsOutboxResultApplier.MAX_ATTEMPTS; i++) {
            smsOutboxPublisher.publishPending();
        }
        // 이 시점에 row는 FAILED 상태

        // when: 한 번 더 폴링
        smsOutboxPublisher.publishPending();

        // then: attempts는 더 증가하지 않아야 한다 (= FAILED는 후속 폴링 대상이 아님)
        SmsOutbox refreshed = smsOutboxRepository.findById(row.getId()).orElseThrow();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(refreshed.getStatus()).isEqualTo(SmsOutboxStatus.FAILED);
            softly.assertThat(refreshed.getAttempts())
                    .as("FAILED row는 폴링 대상에서 제외되므로 attempts 변화 없음")
                    .isEqualTo(SmsOutboxResultApplier.MAX_ATTEMPTS);
        });
    }
}
