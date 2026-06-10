package fittoring.infrastructure.sms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import fittoring.application.reservation.repository.SmsOutboxRepository;
import fittoring.application.reservation.sms.SmsOutbox;
import fittoring.application.reservation.sms.SmsOutboxEventType;
import fittoring.domain.model.Phone;
import fittoring.monitoring.sms.SmsOutboxDeliveryMetrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

class SmsOutboxResultApplierTest {

    private final SmsOutboxRepository smsOutboxRepository = mock(SmsOutboxRepository.class);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);

    private SmsOutboxResultApplier resultApplier;
    private ListAppender<ILoggingEvent> logAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        resultApplier = new SmsOutboxResultApplier(
                smsOutboxRepository,
                eventPublisher,
                new SmsOutboxDeliveryMetrics(new SimpleMeterRegistry())
        );
        logger = (Logger) LoggerFactory.getLogger(SmsOutboxResultApplier.class);
        logAppender = new ListAppender<>();
        logAppender.start();
        logger.addAppender(logAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(logAppender);
    }

    @DisplayName("발송 성공 시 특정 건 지연 검증용 구조화 로그를 남긴다.")
    @Test
    void applySuccessWritesStructuredDeliveryLog() {
        // given: 90초 전에 생성된 outbox row가 발송에 성공한다.
        SmsOutbox row = SmsOutbox.pending(
                7L,
                SmsOutboxEventType.RESERVATION_CREATED,
                new Phone("010-1234-5678"),
                "예약 알림 메시지",
                "핏토링 예약 알림"
        );
        ReflectionTestUtils.setField(row, "id", 42L);
        ReflectionTestUtils.setField(row, "createdAt", LocalDateTime.now().minusSeconds(90));
        when(smsOutboxRepository.findById(42L)).thenReturn(Optional.of(row));

        // when
        resultApplier.applySuccess(42L);

        // then: outboxId, reservationId, eventType, createdAt, sentAt, deliveryLatencyMs를 한 줄로 남긴다.
        assertThat(logAppender.list)
                .map(ILoggingEvent::getFormattedMessage)
                .anySatisfy(message -> assertThat(message)
                        .contains("outboxId=42")
                        .contains("reservationId=7")
                        .contains("eventType=RESERVATION_CREATED")
                        .contains("createdAt=")
                        .contains("sentAt=")
                        .containsPattern("deliveryLatencyMs=9\\d{4}"));
    }
}
