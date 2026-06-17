package fittoring.infrastructure.sms;

import fittoring.application.reservation.sms.SmsOutboxCreatedEvent;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 예약 커밋 직후 outboxId를 SQS로 발행하는 빠른 길.
 * AFTER_COMMIT이라 롤백된 예약은 발행하지 않으며, 발행 실패는 폴러 안전망에 위임한다(throw 안 함).
 */
@Slf4j
@Component
@Profile({"!local & !test"})
@RequiredArgsConstructor
public class SmsOutboxSqsDispatcher {

    private final SqsTemplate sqsTemplate;

    @Value("${aws.sqs.sms-dispatch-queue}")
    private String queueName;

    @Value("${sms.dispatch.sqs.enabled:false}")
    private boolean enabled;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOutboxCreated(SmsOutboxCreatedEvent event) {
        if (!enabled) {
            return;
        }
        try {
            sqsTemplate.send(queueName, event.outboxId());
        } catch (Exception e) {
            log.warn("SMS dispatch SQS 발행 실패, 폴러가 회수: outboxId={}", event.outboxId(), e);
        }
    }
}
