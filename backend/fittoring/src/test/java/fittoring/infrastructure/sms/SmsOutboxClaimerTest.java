package fittoring.infrastructure.sms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import fittoring.application.reservation.repository.SmsOutboxRepository;
import fittoring.application.reservation.sms.SmsOutbox;
import fittoring.application.reservation.sms.SmsOutboxEventType;
import fittoring.application.reservation.sms.SmsOutboxStatus;
import fittoring.domain.model.Phone;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class SmsOutboxClaimerTest {

    private static final int BATCH_SIZE = 10;
    private static final int LEASE_TIMEOUT_SECONDS = 300;

    private final SmsOutboxRepository smsOutboxRepository = mock(SmsOutboxRepository.class);
    private final SmsOutboxClaimer claimer =
            new SmsOutboxClaimer(smsOutboxRepository, BATCH_SIZE, LEASE_TIMEOUT_SECONDS);

    @DisplayName("주어진 id 중 claim 가능한 행을 PROCESSING으로 선점하고, lease cutoff로 조회한다.")
    @Test
    void claimByIds() {
        SmsOutbox row = SmsOutbox.pending(
                7L,
                SmsOutboxEventType.RESERVATION_CREATED,
                new Phone("010-1234-5678"),
                "메시지",
                "핏토링 예약 알림"
        );
        List<Long> ids = List.of(1L, 2L, 3L);
        when(smsOutboxRepository.findClaimableByIds(eq(ids), any(LocalDateTime.class)))
                .thenReturn(List.of(row));

        LocalDateTime before = LocalDateTime.now();
        List<SmsOutbox> claimed = claimer.claimByIds(ids);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(claimed).containsExactly(row);
            softly.assertThat(row.getStatus()).isEqualTo(SmsOutboxStatus.PROCESSING);
            softly.assertThat(row.getProcessingStartedAt()).isNotNull();
        });

        ArgumentCaptor<LocalDateTime> cutoffCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(smsOutboxRepository).findClaimableByIds(eq(ids), cutoffCaptor.capture());
        assertThat(cutoffCaptor.getValue()).isBetween(
                before.minusSeconds(LEASE_TIMEOUT_SECONDS + 5),
                LocalDateTime.now().minusSeconds(LEASE_TIMEOUT_SECONDS - 5)
        );
    }

    @DisplayName("id 목록이 비면 DB 조회 없이 빈 결과를 반환한다.")
    @Test
    void claimByIdsWithEmptyIds() {
        List<SmsOutbox> claimed = claimer.claimByIds(List.of());

        assertThat(claimed).isEmpty();
        verifyNoInteractions(smsOutboxRepository);
    }
}
