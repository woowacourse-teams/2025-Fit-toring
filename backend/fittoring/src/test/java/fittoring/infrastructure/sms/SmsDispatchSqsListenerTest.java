package fittoring.infrastructure.sms;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fittoring.application.reservation.sms.SmsOutbox;
import fittoring.application.reservation.sms.SmsOutboxEventType;
import fittoring.domain.model.Phone;
import fittoring.monitoring.sms.SmsDispatchSqsMetrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SmsDispatchSqsListenerTest {

    private final SmsOutboxClaimer claimer = mock(SmsOutboxClaimer.class);
    private final SmsOutboxPublisher publisher = mock(SmsOutboxPublisher.class);
    private final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
    private final SmsDispatchSqsMetrics metrics = new SmsDispatchSqsMetrics(meterRegistry);
    private final SmsDispatchSqsListener listener = new SmsDispatchSqsListener(claimer, publisher, metrics);

    @DisplayName("수신한 id들을 claim해 발송에 위임한다.")
    @Test
    void handleClaimsAndDispatches() {
        List<Long> ids = List.of(1L, 2L, 3L);
        List<SmsOutbox> claimed = List.of(SmsOutbox.pending(
                7L,
                SmsOutboxEventType.RESERVATION_CREATED,
                new Phone("010-1234-5678"),
                "메시지",
                "핏토링 예약 알림"
        ));
        when(claimer.claimByIds(ids)).thenReturn(claimed);

        listener.handle(ids);

        verify(publisher).dispatch(claimed);
    }

    @DisplayName("claim 결과가 비면(이미 처리됨) 발송하지 않는다.")
    @Test
    void handleDoesNotDispatchWhenNothingClaimed() {
        when(claimer.claimByIds(anyList())).thenReturn(List.of());

        listener.handle(List.of(1L));

        verify(publisher, never()).dispatch(any());
    }
}
