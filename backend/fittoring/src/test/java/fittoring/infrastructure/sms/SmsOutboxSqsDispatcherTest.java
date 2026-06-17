package fittoring.infrastructure.sms;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import fittoring.application.reservation.sms.SmsOutboxCreatedEvent;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class SmsOutboxSqsDispatcherTest {

    private static final String QUEUE = "sms-dispatch-queue";

    private final SqsTemplate sqsTemplate = mock(SqsTemplate.class);
    private SmsOutboxSqsDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        dispatcher = new SmsOutboxSqsDispatcher(sqsTemplate);
        ReflectionTestUtils.setField(dispatcher, "queueName", QUEUE);
    }

    @DisplayName("플래그가 켜져 있으면 outboxId를 큐로 발행한다.")
    @Test
    void publishWhenEnabled() {
        ReflectionTestUtils.setField(dispatcher, "enabled", true);

        dispatcher.onOutboxCreated(new SmsOutboxCreatedEvent(42L));

        verify(sqsTemplate).send(QUEUE, 42L);
    }

    @DisplayName("플래그가 꺼져 있으면 발행하지 않는다.")
    @Test
    void doNotPublishWhenDisabled() {
        ReflectionTestUtils.setField(dispatcher, "enabled", false);

        dispatcher.onOutboxCreated(new SmsOutboxCreatedEvent(42L));

        verify(sqsTemplate, never()).send(anyString(), any());
    }

    @DisplayName("SQS 발행이 실패해도 예외를 전파하지 않는다(폴러 안전망에 위임).")
    @Test
    void swallowSqsFailure() {
        ReflectionTestUtils.setField(dispatcher, "enabled", true);
        doThrow(new RuntimeException("SQS down")).when(sqsTemplate).send(QUEUE, 42L);

        assertThatCode(() -> dispatcher.onOutboxCreated(new SmsOutboxCreatedEvent(42L)))
                .doesNotThrowAnyException();
    }
}
