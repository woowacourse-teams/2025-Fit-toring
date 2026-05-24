package fittoring.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.application.exception.SmsOutboxNotRetryableException;
import java.time.LocalDateTime;

import fittoring.application.reservation.sms.SmsOutbox;
import fittoring.application.reservation.sms.SmsOutboxEventType;
import fittoring.application.reservation.sms.SmsOutboxStatus;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SmsOutboxTest {

    private static final int MAX_ATTEMPTS = 3;
    private static final String SUBJECT = "핏토링 예약 알림";

    @DisplayName("PENDING row를 PROCESSING으로 선점하면 status가 PROCESSING이 되고 processingStartedAt이 채워진다.")
    @Test
    void markProcessingTransitionsPendingRow() {
        // given
        SmsOutbox row = pendingRow();
        LocalDateTime now = LocalDateTime.of(2026, 5, 20, 12, 0);

        // when
        row.markProcessing(now);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(row.getStatus()).isEqualTo(SmsOutboxStatus.PROCESSING);
            softly.assertThat(row.getProcessingStartedAt()).isEqualTo(now);
        });
    }

    @DisplayName("PROCESSING row가 발송 성공으로 SENT가 되면 processingStartedAt이 null로 정리된다.")
    @Test
    void markSentClearsProcessingStartedAt() {
        // given
        SmsOutbox row = pendingRow();
        row.markProcessing(LocalDateTime.of(2026, 5, 20, 12, 0));

        // when
        row.markSent();

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(row.getStatus()).isEqualTo(SmsOutboxStatus.SENT);
            softly.assertThat(row.getProcessingStartedAt()).isNull();
        });
    }

    @DisplayName("이전에 실패 이력이 있어도 markSent가 호출되면 lastError가 null로 정리된다.")
    @Test
    void markSentClearsLastError() {
        // given: 한 번 실패해 lastError가 남은 row가 재시도에서 성공하는 시나리오
        SmsOutbox row = pendingRow();
        row.markProcessing(LocalDateTime.of(2026, 5, 20, 12, 0));
        row.recordFailure("일시적 실패", MAX_ATTEMPTS);
        row.markProcessing(LocalDateTime.of(2026, 5, 20, 12, 5));

        // when
        row.markSent();

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(row.getStatus()).isEqualTo(SmsOutboxStatus.SENT);
            softly.assertThat(row.getLastError())
                    .as("SENT 전이 후에는 이전 실패 사유가 잔류하면 안 된다")
                    .isNull();
        });
    }

    @DisplayName("PROCESSING row가 실패하면 attempts 미만일 때 PENDING으로 되돌아가고 processingStartedAt이 null로 정리된다.")
    @Test
    void recordFailureBeforeMaxAttemptsReturnsToPending() {
        // given
        SmsOutbox row = pendingRow();
        row.markProcessing(LocalDateTime.of(2026, 5, 20, 12, 0));

        // when
        row.recordFailure("일시적 실패", MAX_ATTEMPTS);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(row.getStatus()).isEqualTo(SmsOutboxStatus.PENDING);
            softly.assertThat(row.getAttempts()).isEqualTo(1);
            softly.assertThat(row.getLastError()).isEqualTo("일시적 실패");
            softly.assertThat(row.getProcessingStartedAt()).isNull();
        });
    }

    @DisplayName("PROCESSING row가 attempts 한도에 도달하면 FAILED가 되고 processingStartedAt이 null로 정리된다.")
    @Test
    void recordFailureAtMaxAttemptsBecomesFailed() {
        // given
        SmsOutbox row = pendingRow();

        // when: 3회 실패 (각 실패 직전에 PROCESSING으로 선점)
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            row.markProcessing(LocalDateTime.of(2026, 5, 20, 12, i));
            row.recordFailure("계속 실패", MAX_ATTEMPTS);
        }

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(row.getStatus()).isEqualTo(SmsOutboxStatus.FAILED);
            softly.assertThat(row.getAttempts()).isEqualTo(MAX_ATTEMPTS);
            softly.assertThat(row.getProcessingStartedAt()).isNull();
        });
    }

    @DisplayName("recordFailure는 maxAttempts가 0 이하면 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void recordFailureRejectsNonPositiveMaxAttempts(int invalid) {
        // given
        SmsOutbox row = pendingRow();

        // when //then
        assertThatThrownBy(() -> row.recordFailure("error", invalid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maxAttempts");
    }

    @DisplayName("새로 만든 PENDING row는 processingStartedAt이 null이다.")
    @Test
    void freshPendingRowHasNullProcessingStartedAt() {
        // given //when
        SmsOutbox row = pendingRow();

        // then
        assertThat(row.getProcessingStartedAt()).isNull();
    }

    @DisplayName("새로 만든 PENDING row는 failedNotifiedAt이 null이다.")
    @Test
    void freshPendingRowHasNullFailedNotifiedAt() {
        // given //when
        SmsOutbox row = pendingRow();

        // then
        assertThat(row.getFailedNotifiedAt()).isNull();
    }

    @DisplayName("recordFailure는 FAILED 전환이 처음 발생하고 아직 알림 전이라면 true를 반환한다.")
    @Test
    void recordFailureReturnsTrueOnFirstFailedTransition() {
        // given: maxAttempts 직전까지 실패한 row
        SmsOutbox row = pendingRow();
        for (int i = 0; i < MAX_ATTEMPTS - 1; i++) {
            row.markProcessing(LocalDateTime.of(2026, 5, 20, 12, i));
            boolean transitioned = row.recordFailure("일시적 실패", MAX_ATTEMPTS);
            assertThat(transitioned)
                    .as("PENDING 유지 단계에서는 FAILED 전환이 아니므로 false를 반환해야 한다")
                    .isFalse();
        }
        row.markProcessing(LocalDateTime.of(2026, 5, 20, 12, MAX_ATTEMPTS));

        // when: 마지막 한 번 더 실패해 FAILED로 전환
        boolean transitioned = row.recordFailure("최종 실패", MAX_ATTEMPTS);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(transitioned)
                    .as("FAILED 전환 직후이며 아직 Slack 알림 전이므로 true를 반환해야 한다")
                    .isTrue();
            softly.assertThat(row.getStatus()).isEqualTo(SmsOutboxStatus.FAILED);
            softly.assertThat(row.getFailedNotifiedAt()).isNull();
        });
    }

    @DisplayName("markFailedNotified가 호출되면 failedNotifiedAt이 채워진다.")
    @Test
    void markFailedNotifiedStampsNotifiedAt() {
        // given: FAILED 상태의 row
        SmsOutbox row = pendingRow();
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            row.markProcessing(LocalDateTime.of(2026, 5, 20, 12, i));
            row.recordFailure("계속 실패", MAX_ATTEMPTS);
        }
        LocalDateTime notifiedAt = LocalDateTime.of(2026, 5, 20, 12, 30);

        // when
        row.markFailedNotified(notifiedAt);

        // then
        assertThat(row.getFailedNotifiedAt()).isEqualTo(notifiedAt);
    }

    @DisplayName("retryManually는 FAILED row를 PENDING으로 되돌리고 누적 상태를 초기화한다.")
    @Test
    void retryManuallyResetsFailedRow() {
        // given: 3회 실패 후 Slack 알림까지 끝난 FAILED row
        SmsOutbox row = pendingRow();
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            row.markProcessing(LocalDateTime.of(2026, 5, 20, 12, i));
            row.recordFailure("계속 실패", MAX_ATTEMPTS);
        }
        row.markFailedNotified(LocalDateTime.of(2026, 5, 20, 12, 30));

        // when
        row.retryManually();

        // then: publisher가 다시 집어 갈 수 있도록 PENDING + attempts/lastError/lease/notified 초기화
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(row.getStatus()).isEqualTo(SmsOutboxStatus.PENDING);
            softly.assertThat(row.getAttempts()).isZero();
            softly.assertThat(row.getLastError()).isNull();
            softly.assertThat(row.getProcessingStartedAt()).isNull();
            softly.assertThat(row.getFailedNotifiedAt())
                    .as("다시 3회 실패하면 Slack이 재발송될 수 있도록 알림 시각도 초기화돼야 한다")
                    .isNull();
        });
    }

    @DisplayName("retryManually는 FAILED가 아닌 status에서는 SmsOutboxNotRetryableException을 던진다.")
    @Test
    void retryManuallyRejectsNonFailedRow() {
        // given: PENDING row (아직 FAILED 아님)
        SmsOutbox row = pendingRow();

        // when //then
        assertThatThrownBy(row::retryManually)
                .isInstanceOf(SmsOutboxNotRetryableException.class)
                .hasMessageContaining("FAILED");
    }

    private SmsOutbox pendingRow() {
        return SmsOutbox.pending(
                1L,
                SmsOutboxEventType.RESERVATION_CREATED,
                new Phone("010-0000-0001"),
                "메시지 본문",
                SUBJECT
        );
    }
}
