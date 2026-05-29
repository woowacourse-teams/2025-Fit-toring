package fittoring.infrastructure;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import fittoring.infrastructure.sms.SmsOutboxClaimer;
import fittoring.infrastructure.sms.SmsOutboxPublisher;
import fittoring.infrastructure.sms.SmsOutboxResultApplier;
import fittoring.infrastructure.sms.SmsRestClientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class SmsOutboxPublisherShutdownTest {

    @DisplayName("graceful shutdown 신호가 오면 publishPending은 claim service를 호출하지 않는다.")
    @Test
    void publishPendingSkipsAfterShutdown() {
        // given
        SmsOutboxClaimer claimService = mock(SmsOutboxClaimer.class);
        SmsRestClientService smsRestClientService = mock(SmsRestClientService.class);
        SmsOutboxResultApplier resultApplier = mock(SmsOutboxResultApplier.class);
        SmsOutboxPublisher publisher = new SmsOutboxPublisher(claimService, smsRestClientService, resultApplier);

        // when
        publisher.onShutdown();
        publisher.publishPending();

        // then
        verify(claimService, never()).claimPending();
    }

    @DisplayName("graceful shutdown 신호가 오면 스케줄러 진입도 차단된다.")
    @Test
    void runScheduledSkipsAfterShutdown() {
        // given
        SmsOutboxClaimer claimService = mock(SmsOutboxClaimer.class);
        SmsRestClientService smsRestClientService = mock(SmsRestClientService.class);
        SmsOutboxResultApplier resultApplier = mock(SmsOutboxResultApplier.class);
        SmsOutboxPublisher publisher = new SmsOutboxPublisher(claimService, smsRestClientService, resultApplier);
        ReflectionTestUtils.setField(publisher, "enabled", true);

        // when
        publisher.onShutdown();
        publisher.runScheduled();

        // then
        verify(claimService, never()).claimPending();
    }
}
