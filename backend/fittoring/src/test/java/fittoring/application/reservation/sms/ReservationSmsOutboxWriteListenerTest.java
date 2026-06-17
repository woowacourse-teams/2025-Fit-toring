package fittoring.application.reservation.sms;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fittoring.application.reservation.repository.SmsOutboxRepository;
import fittoring.application.reservation.service.event.ReservationCreatedEvent;
import fittoring.domain.model.Phone;
import fittoring.infrastructure.sms.SmsMessageFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

class ReservationSmsOutboxWriteListenerTest {

    private final SmsOutboxRepository smsOutboxRepository = mock(SmsOutboxRepository.class);
    private final SmsMessageFormatter smsMessageFormatter = mock(SmsMessageFormatter.class);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final ReservationSmsOutboxWriteListener listener =
            new ReservationSmsOutboxWriteListener(smsOutboxRepository, smsMessageFormatter, eventPublisher);

    @DisplayName("예약 신청 outbox 저장 후 저장된 id로 SmsOutboxCreatedEvent를 발행한다.")
    @Test
    void publishesCreatedEventWithSavedId() {
        when(smsMessageFormatter.reservationMessage(any(), any())).thenReturn("메시지");
        SmsOutbox saved = SmsOutbox.pending(
                7L,
                SmsOutboxEventType.RESERVATION_CREATED,
                new Phone("010-1234-5678"),
                "메시지",
                "핏토링 예약 알림"
        );
        ReflectionTestUtils.setField(saved, "id", 99L);
        when(smsOutboxRepository.save(any(SmsOutbox.class))).thenReturn(saved);

        listener.onReservationCreated(new ReservationCreatedEvent(
                7L, "멘티", "내용", new Phone("010-9999-8888")));

        verify(smsOutboxRepository).save(any(SmsOutbox.class));
        verify(eventPublisher).publishEvent(new SmsOutboxCreatedEvent(99L));
    }
}
