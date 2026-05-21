package fittoring.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
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
