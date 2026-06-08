package fittoring.infrastructure.chat;

import fittoring.application.chat.service.ChatMessagePersistenceService;
import fittoring.application.chat.service.dto.ChatMessagePersistEventDto;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.validation.Valid;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Profile({"!local & !test"})
@Component
public class ChatMessagePersistSqsListener {

    private final ChatMessagePersistenceService chatMessagePersistenceService;
    private final MeterRegistry meterRegistry;
    private final Timer consumeTimer;
    private final AtomicInteger activeConsumes = new AtomicInteger();

    public ChatMessagePersistSqsListener(
            ChatMessagePersistenceService chatMessagePersistenceService,
            MeterRegistry meterRegistry
    ) {
        this.chatMessagePersistenceService = chatMessagePersistenceService;
        this.meterRegistry = meterRegistry;
        this.consumeTimer = Timer.builder("chat_persist_sqs_listener_seconds")
                .description("End-to-end latency of chat persist SQS listener processing")
                .publishPercentileHistogram(true)
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);
        Gauge.builder("chat_persist_sqs_listener_active", activeConsumes, AtomicInteger::get)
                .description("Current active chat persist SQS listener executions")
                .register(meterRegistry);
    }

    @SqsListener(
            value = "${aws.sqs.chat-message-persist-queue}",
            maxConcurrentMessages = "${chat.persist.sqs.max-concurrent-messages}",
            maxMessagesPerPoll = "${chat.persist.sqs.max-messages-per-poll}"
    )
    public void handle(@Valid @Payload ChatMessagePersistEventDto event) {
        Timer.Sample sample = Timer.start(meterRegistry);
        activeConsumes.incrementAndGet();
        try {
            boolean persisted = chatMessagePersistenceService.persist(event);
            if (persisted) {
                meterRegistry.counter("chat_persist_success_total").increment();
            }
            meterRegistry.counter("chat_persist_sqs_listener_total", "result", "success").increment();
        } catch (Exception e) {
            meterRegistry.counter(
                    "chat_persist_failure_total",
                    "error_type",
                    e.getClass().getSimpleName()
            ).increment();
            meterRegistry.counter("chat_persist_sqs_listener_total", "result", "failure").increment();
            meterRegistry.counter(
                    "chat_persist_sqs_listener_failure_total",
                    "error_type",
                    e.getClass().getSimpleName()
            ).increment();
            throw e;
        } finally {
            activeConsumes.decrementAndGet();
            sample.stop(consumeTimer);
        }
    }
}
