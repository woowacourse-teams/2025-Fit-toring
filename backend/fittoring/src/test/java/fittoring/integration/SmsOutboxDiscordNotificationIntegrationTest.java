package fittoring.integration;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import fittoring.AbstractApiDocumentationTest;
import fittoring.application.reservation.repository.SmsOutboxRepository;
import fittoring.domain.model.Phone;
import fittoring.application.reservation.sms.SmsOutbox;
import fittoring.application.reservation.sms.SmsOutboxEventType;
import fittoring.application.reservation.sms.SmsOutboxStatus;
import fittoring.infrastructure.SmsOutboxPublisher;
import fittoring.infrastructure.SmsOutboxResultApplier;
import fittoring.infrastructure.exception.InfraErrorMessage;
import fittoring.infrastructure.exception.SmsException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SmsOutboxDiscordNotificationIntegrationTest extends AbstractApiDocumentationTest {

    private static final String SUBJECT = "핏토링 예약 알림";

    @Autowired
    private SmsOutboxRepository smsOutboxRepository;

    @Autowired
    private SmsOutboxPublisher smsOutboxPublisher;

    @DisplayName("MAX_ATTEMPTS회 실패로 FAILED로 전환되면 Discord 알림이 정확히 1회 발송되고 failedNotifiedAt이 기록된다.")
    @Test
    void failedRowTriggersDiscordOnce() {
        // given: 모든 발송이 실패하도록 stub
        doThrow(new SmsException("알림 테스트용 실패"))
                .when(smsRestClientService).sendBatch(anyList());
        SmsOutbox row = smsOutboxRepository.save(pendingRow("010-0000-0001"));

        // when: MAX_ATTEMPTS회 폴링
        for (int i = 0; i < SmsOutboxResultApplier.MAX_ATTEMPTS; i++) {
            smsOutboxPublisher.publishPending();
        }

        // then: FAILED 전환 직후 Discord가 정확히 1회 호출되어야 한다.
        verify(discordWebhookClient, times(1)).send(anyString());
        SmsOutbox refreshed = smsOutboxRepository.findById(row.getId()).orElseThrow();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(refreshed.getStatus()).isEqualTo(SmsOutboxStatus.FAILED);
            softly.assertThat(refreshed.getFailedNotifiedAt())
                    .as("Discord 발송 성공 시 failedNotifiedAt이 기록되어 재알림이 차단된다")
                    .isNotNull();
        });
    }

    @DisplayName("Discord 호출이 실패해도 FAILED row 상태는 유지되며 failedNotifiedAt만 null로 남는다.")
    @Test
    void discordFailureDoesNotRollbackFailedStatus() {
        // given: 발송 실패 + Discord도 throw
        doThrow(new SmsException(InfraErrorMessage.SMS_SERVER_ERROR.getMessage()))
                .when(smsRestClientService).sendBatch(anyList());
        doThrow(new RuntimeException("discord down"))
                .when(discordWebhookClient).send(anyString());
        SmsOutbox row = smsOutboxRepository.save(pendingRow("010-0000-0001"));

        // when
        for (int i = 0; i < SmsOutboxResultApplier.MAX_ATTEMPTS; i++) {
            smsOutboxPublisher.publishPending();
        }

        // then: Discord 예외는 listener 내부에서 swallow되어 outbox 상태에 영향 없음
        SmsOutbox refreshed = smsOutboxRepository.findById(row.getId()).orElseThrow();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(refreshed.getStatus())
                    .as("Discord 장애가 FAILED 전환을 롤백시키지 않는다")
                    .isEqualTo(SmsOutboxStatus.FAILED);
            softly.assertThat(refreshed.getFailedNotifiedAt())
                    .as("Discord 발송이 실패한 경우 failedNotifiedAt은 채워지지 않아 추후 재시도/감사가 가능하다")
                    .isNull();
        });
    }

    @DisplayName("PENDING 유지 단계(아직 FAILED 미전환)에서는 Discord를 호출하지 않는다.")
    @Test
    void pendingTransitionDoesNotTriggerDiscord() {
        // given: 1회만 폴링 → attempts=1, status=PENDING (FAILED 미전환)
        doThrow(new SmsException(InfraErrorMessage.SMS_SERVER_ERROR.getMessage()))
                .when(smsRestClientService).sendBatch(anyList());
        SmsOutbox row = smsOutboxRepository.save(pendingRow("010-0000-0001"));

        // when
        smsOutboxPublisher.publishPending();

        // then
        verifyNoInteractions(discordWebhookClient);
        SmsOutbox refreshed = smsOutboxRepository.findById(row.getId()).orElseThrow();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(refreshed.getStatus()).isEqualTo(SmsOutboxStatus.PENDING);
            softly.assertThat(refreshed.getFailedNotifiedAt()).isNull();
        });
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
}
